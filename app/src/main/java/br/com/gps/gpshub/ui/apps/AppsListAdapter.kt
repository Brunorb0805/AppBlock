package br.com.gps.gpshub.ui.apps

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.gps.gpshub.databinding.ItemListAppsBinding
import br.com.gps.gpshub.model.entity.Apps
import com.bumptech.glide.Glide


class AppsListAdapter(private val viewModel: AppsViewModel) :
    ListAdapter<Apps, AppsListAdapter.ViewHolder>(AppsDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }


    class ViewHolder private constructor(private val binding: ItemListAppsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: AppsViewModel, app: Apps) {

            try {
                Glide.with(binding.appImageView)
                    .load(if (!app.isLocalApp) app.iconDrawable else app.icon)
                    .into(binding.appImageView)
            } catch (e: Exception) {
            }

            binding.viewmodel = viewModel
            binding.model = app
            binding.executePendingBindings()
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListAppsBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class AppsDiffCallback : DiffUtil.ItemCallback<Apps>() {
    override fun areItemsTheSame(oldItem: Apps, newItem: Apps): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: Apps, newItem: Apps): Boolean {
        return oldItem == newItem
    }
}