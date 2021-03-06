package id.gits.kade.ibun.schedules

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.gits.kade.ibun.data.Match
import id.gits.kade.ibun.match.MatchActivity
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.support.v4.toast

class SchedulesFragment : Fragment(), SchedulesContract.View {
    override lateinit var presenter: SchedulesContract.Presenter

    private lateinit var swipeLayout: SwipeRefreshLayout

    val items: ArrayList<Match> = arrayListOf()

    var type: SchedulesActivity.TYPE = SchedulesActivity.TYPE.PAST

    private lateinit var listAdapter: SchedulesAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = ScheduleFragmentUI().createView(AnkoContext.create(ctx, this))

        listAdapter = SchedulesAdapter(items, object : SchedulesAdapter.ScheduleItemListener {
            override fun onMatchClick(clickedMatch: Match) {
                showMatchDetailUi(clickedMatch)
            }
        })

        with(view.findViewById<RecyclerView>(ScheduleFragmentUI.recyclerViewId)) {
            adapter = listAdapter
        }

        with(view.findViewById<SwipeRefreshLayout>(ScheduleFragmentUI.swipeRefreshId)) {
            swipeLayout = this
            setOnRefreshListener { presenter.getMatches() }
        }

        arguments?.getSerializable(ARGUMENT_TYPE)?.let {
            type = it as SchedulesActivity.TYPE
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        presenter.setType(type)
        presenter.start()
    }

    override fun showError(message: String?) {
        message?.let { toast(it) }
    }

    override fun showMatches(matches: List<Match>) {
        items.clear()
        items.addAll(matches)
        listAdapter.notifyDataSetChanged()
    }

    override fun showLoading() {
        swipeLayout.isRefreshing = true
    }

    override fun hideLoading() {
        swipeLayout.isRefreshing = false
    }

    override fun showMatchDetailUi(match: Match) {
        context?.startActivity<MatchActivity>(MatchActivity.EXTRA_MATCH to match)
    }

    companion object {

        private const val ARGUMENT_TYPE = "TYPE"

        fun newInstance(type: SchedulesActivity.TYPE) =
                SchedulesFragment().apply {
                    //                    arguments = Bundle().apply { putBoolean(ARGUMENT_IS_PAST, isPast) }
                    arguments = Bundle().apply { putSerializable(ARGUMENT_TYPE, type) }
                }
    }

    class ScheduleFragmentUI : AnkoComponent<Fragment> {
        companion object {
            const val swipeRefreshId = 1
            const val recyclerViewId = 2
        }

        override fun createView(ui: AnkoContext<Fragment>) = with(ui) {
            swipeRefreshLayout {
                id = swipeRefreshId

                recyclerView {
                    id = recyclerViewId
                    layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }
}