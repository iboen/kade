package id.gits.kade.ibun.match

import id.gits.kade.ibun.BasePresenter
import id.gits.kade.ibun.BaseView
import id.gits.kade.ibun.data.Team

/**
 * This specifies the contract between the view and the presenter.
 */
interface MatchContract {
    interface View : BaseView<Presenter> {
        fun showClubHome(team: Team)
        fun showClubAway(team: Team)
        fun showLoading()
        fun hideLoading()
        fun showAddFavoriteSuccess()
        fun showRemoveFavoriteSuccess()
        fun showToggleFavoriteError()
        fun invalidateMenu()
    }

    interface Presenter : BasePresenter {
        fun getClub()
        fun isFavorite(): Boolean
        fun addToFavorite()
        fun removeFromFavorite()
    }
}