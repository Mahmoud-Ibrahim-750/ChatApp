package com.mis.route.chatapp.ui.fragments.createroom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.mis.route.chatapp.databinding.ItemRoomCategoryBinding
import com.mis.route.chatapp.ui.model.RoomCategory

class RoomCategorySpinnerAdapter(private val itemsList: List<RoomCategory>) : BaseAdapter() {
    override fun getCount() = itemsList.size

    override fun getItem(position: Int) = itemsList[position]

    override fun getItemId(position: Int) = itemsList[position].id.toLong()

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        // create a view holder if not created before
        val viewHolder: ViewHolder
        if (view == null) {
            val viewBinding = ItemRoomCategoryBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent, false
            )
            viewHolder = ViewHolder(viewBinding)
            viewBinding.root.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        // bind data
        val category = itemsList[position]
        viewHolder.viewBinding.image.setImageResource(category.imageResId)
        viewHolder.viewBinding.title.text = category.title

        return viewHolder.viewBinding.root
    }

    class ViewHolder(val viewBinding: ItemRoomCategoryBinding)
}