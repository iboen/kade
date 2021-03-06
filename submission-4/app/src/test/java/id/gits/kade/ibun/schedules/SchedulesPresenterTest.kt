package id.gits.kade.ibun.schedules

import id.gits.kade.ibun.argumentCaptor
import id.gits.kade.ibun.capture
import id.gits.kade.ibun.data.Match
import id.gits.kade.ibun.data.source.SportsDataSource
import id.gits.kade.ibun.data.source.SportsRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.*

class SchedulesPresenterTest {
    @Mock
    private lateinit var sportsRepository: SportsRepository
    @Mock
    private lateinit var view: SchedulesContract.View

    private lateinit var presenter: SchedulesPresenter

    @Captor
    private lateinit var loadTasksCallbackCaptor: ArgumentCaptor<SportsDataSource.LoadMatchesCallback>

    private lateinit var matches: MutableList<Match>

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)

        presenter = SchedulesPresenter(sportsRepository, view)
        matches = arrayListOf(
                Match("1", "1", "2",
                        "Persikasi", "Persib", strDate = "12/12/2012",
                        strTime = "14:00:00+00:00", isPast = false),
                Match("1", "3", "4",
                        "AC Milan", "PSJ", strDate = "14/12/2012",
                        strTime = "14:00:00+00:00", isPast = false))
    }

    @Test
    fun createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test
        presenter = SchedulesPresenter(sportsRepository, view)

        // Then the presenter is set to the view
        verify(view).presenter = presenter
    }

    @Test
    fun loadNextMatchesFromRepositoryAndLoadIntoView() {
        with(presenter) {
            setType(SchedulesActivity.TYPE.NEXT)
            getMatches()
        }

        // Callback is captured and invoked with stubbed tasks
        verify(sportsRepository).getNextMatches(capture(loadTasksCallbackCaptor))
        loadTasksCallbackCaptor.value.onMatchesLoaded(matches)

        val inOrder = inOrder(view)
        inOrder.verify(view).showLoading()
        inOrder.verify(view).hideLoading()

        val getMatchesArgumentCaptor = argumentCaptor<List<Match>>()
        verify(view).showMatches(capture(getMatchesArgumentCaptor))

        assertTrue(getMatchesArgumentCaptor.value.size == 2)
    }

    @Test
    fun loadPastMatchesFromRepositoryAndLoadIntoView() {
        with(presenter) {
            setType(SchedulesActivity.TYPE.PAST)
            getMatches()
        }

        // Callback is captured and invoked with stubbed tasks
        verify(sportsRepository).getLastMatches(capture(loadTasksCallbackCaptor))
        loadTasksCallbackCaptor.value.onMatchesLoaded(matches)

        val inOrder = inOrder(view)
        inOrder.verify(view).showLoading()
        inOrder.verify(view).hideLoading()

        val getMatchesArgumentCaptor = argumentCaptor<List<Match>>()
        verify(view).showMatches(capture(getMatchesArgumentCaptor))

        assertTrue(getMatchesArgumentCaptor.value.size == 2)
    }

    @Test
    fun loadAnyMatchesFromRepositoryAndShowError() {
        with(presenter) {
            setType(SchedulesActivity.TYPE.NEXT)
            getMatches()
        }

        // Callback is captured and invoked with stubbed tasks
        verify(sportsRepository).getNextMatches(capture(loadTasksCallbackCaptor))
        val error = "Unknown error"
        loadTasksCallbackCaptor.value.onError(error)

        val inOrder = inOrder(view)
        inOrder.verify(view).showLoading()
        inOrder.verify(view).hideLoading()

        val getMatchesArgumentCaptor = argumentCaptor<String>()
        verify(view).showError(capture(getMatchesArgumentCaptor))

        assertTrue(getMatchesArgumentCaptor.value == error)
    }

}