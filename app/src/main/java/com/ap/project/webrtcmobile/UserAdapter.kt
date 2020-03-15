package com.ap.project.webrtcmobile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ap.project.webrtcmobile.databinding.UserInfoRowBinding
import com.ap.project.webrtcmobile.models.ClientInfo

class UserAdapter(private val userAdapterCallback: UserAdapterCallback) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val models = ArrayList<ClientInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(UserInfoRowBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = models.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindView(models[position])
    }


    fun addModel(model: ClientInfo) {
        models.add(model)
        notifyItemInserted(models.size - 1)
    }

    fun replaceModel(newModels: List<ClientInfo>) {
        models.clear()
        models.addAll(newModels)
        notifyDataSetChanged()
    }

    fun removeModel(model: ClientInfo) {
        val removed = models.remove(model)
        if (removed) {
            notifyDataSetChanged()
        }
    }


    inner class UserViewHolder(private val binding: UserInfoRowBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bindView(model: ClientInfo) {
            binding.username.text = model.username
            binding.callUser.setOnClickListener {
                userAdapterCallback.onCallUserInvoked(model)
            }
        }
    }

    interface UserAdapterCallback {
        fun onCallUserInvoked(model: ClientInfo)
    }
}