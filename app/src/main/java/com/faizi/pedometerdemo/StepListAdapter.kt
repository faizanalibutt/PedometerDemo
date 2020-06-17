package com.faizi.pedometerdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StepListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<StepListAdapter.StepViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var steps = emptyList<Step>()

    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stepItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return StepViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val current = steps[position]
        holder.stepItemView.text = current.step
    }

    internal fun setSteps(steps: List<Step>) {
        this.steps = steps
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = steps.size


}