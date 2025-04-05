package com.example.androidplayground.utli

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class BaseRvAdapter<M,H : RecyclerView.ViewHolder> (private val context: Context,
                                                         private val layoutId : Int,
                                                         private val holderMaker :(view : View) -> H,
                                                         private var mutableList: MutableList<M>,
                                                         private var binder : (H,M,Int) -> Unit) : RecyclerView.Adapter<H>() {

    var items: MutableList<M> = mutableList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {

        val itemView = LayoutInflater.from(context).inflate(layoutId,parent,false)
        return holderMaker(itemView)
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: H, position: Int) {

        val item : M = items[position]
        binder(holder,item,position)
    }


    fun updateItems( results: MutableList<M>){
        if(results == null){
            return
        }
        items = results
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): M {
        return items[position]
    }

}