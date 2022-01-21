package br.com.gps.gpshub.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.gps.gpshub.databinding.ItemAppsBinding
import br.com.gps.gpshub.model.entity.Apps
import com.bumptech.glide.Glide


class HomeListAdapter(val viewModel: HomeViewModel) :
    ListAdapter<Apps, HomeListAdapter.ViewHolder>(AppsDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }


    class ViewHolder private constructor(val binding: ItemAppsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: HomeViewModel, app: Apps) {

            try {

                Glide.with(binding.appImageView)
                    .load(if (!app.isLocalApp) app.iconDrawable else app.icon)
                    .into(binding.appImageView)

                if (!app.isLocalApp) {
//                    val uninstallPackage = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)
//                        PackageManager.GET_UNINSTALLED_PACKAGES else PackageManager.MATCH_UNINSTALLED_PACKAGES
//                    val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(
//                        item.packageName, uninstallPackage
//                    )
//                    binding.imageView.setImageDrawable(
//                        packageManager.getApplicationIcon(
//                            applicationInfo
//                        )
//                    )
                    Glide.with(binding.appImageView).load(app.iconDrawable)
                        .into(binding.appImageView)


                } else {
                    app.icon?.let { icon ->
                        if (icon != 0)
                            Glide.with(binding.appImageView).load(icon).into(binding.appImageView)
                    }
                }
            } catch (e: Exception) {

            }

            binding.viewmodel = viewModel
            binding.model = app
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAppsBinding.inflate(layoutInflater, parent, false)

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
