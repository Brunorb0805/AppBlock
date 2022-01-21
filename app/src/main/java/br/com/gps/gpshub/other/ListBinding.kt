package br.com.gps.gpshub.other

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.gps.gpshub.model.entity.Apps
import br.com.gps.gpshub.ui.apps.AppsListAdapter
import br.com.gps.gpshub.ui.home.HomeListAdapter


@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Apps>?) {
    items?.let {
//        (listView.adapter as AppsListAdapter).submitList(items)
        when(listView.adapter) {
            is AppsListAdapter -> (listView.adapter as AppsListAdapter).submitList(items)
            is HomeListAdapter -> (listView.adapter as HomeListAdapter).submitList(items)
        }
    }
}