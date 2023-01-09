package com.example.adminovercooked.user

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.adminovercooked.R
import com.example.adminovercooked.data.model.User

class UserAdapter(
    private val context: Context,
    private val originalList: List<User>
) : BaseAdapter() {

    private var filteredList = ArrayList<User>(originalList)

    override fun getCount() = filteredList.size

    override fun getItem(p0: Int): Any {
        return filteredList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_user, null)
        val targetUser = filteredList[p0]
        if (targetUser.image.isNotEmpty())
            Glide.with(context)
                .load(targetUser.image)
                .into(view.findViewById(R.id.ivProfile))
        view.findViewById<TextView>(R.id.tvProfileName).text = targetUser.username
        return view
    }

    fun filter(): Filter {
        return MyFilter()
    }

    inner class MyFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filterString =
                p0?.toString() ?: ""
            val result = FilterResults()
            val newList = ArrayList<User>()

            for (i in originalList.indices) {
                val user = originalList[i]
                if (user.username.startsWith(filterString, true))
                    newList.add(user)
            }
            result.values = newList
            result.count = newList.size
            return result
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            if (p1!!.values as? ArrayList<User> != null) {
                filteredList = p1.values as ArrayList<User>
                notifyDataSetChanged()
            }
        }
    }
}