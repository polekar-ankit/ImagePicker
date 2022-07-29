package com.tap.gallary.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.BaseAdapter

/**
 * Created by Ankit on 03-11-2016.
 */
abstract class CustomGenericAdapter<T>(
    protected var activity: Activity?,
    protected var context: Context?,
    protected var arrayList: ArrayList<T>
) : BaseAdapter() {
    protected var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    protected var size = 0
    override fun getCount(): Int {
        return arrayList.size
    }

    fun addItem(arrayList: ArrayList<T>?) {
        this.arrayList.addAll(arrayList!!)
    }

    override fun getItem(position: Int): T {
        return arrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setLayoutParams(size: Int) {
        this.size = size
    }

    fun releaseResources() {
        arrayList = ArrayList()
        context = null
        activity = null
    }

}