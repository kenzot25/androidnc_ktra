package deso1.lebatoan.dlu_21a100100382.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deso1.lebatoan.dlu_21a100100382.R
import deso1.lebatoan.dlu_21a100100382.models.Food

class FoodAdapter(
    private var foods: List<Food>,
    private val onEditClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodImage: ImageView = view.findViewById(R.id.foodImage)
        val foodName: TextView = view.findViewById(R.id.foodName)
        val foodPrice: TextView = view.findViewById(R.id.foodPrice)
        val editButton: ImageButton = view.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        with(holder) {
            foodName.text = food.name
            foodPrice.text = String.format("Giá: %,.0f đồng", food.price)

            // Handle both content URIs and drawable resources
            if (food.imageUrl.startsWith("content://")) {
                foodImage.setImageURI(android.net.Uri.parse(food.imageUrl))
            } else {
                val resourceId = itemView.context.resources.getIdentifier(
                    food.imageUrl, "drawable", itemView.context.packageName
                )
                if (resourceId != 0) {
                    foodImage.setImageResource(resourceId)
                }
            }

            editButton.setOnClickListener { onEditClick(food) }
        }
    }

    override fun getItemCount() = foods.size

    fun updateData(newFoods: List<Food>) {
        foods = newFoods
        notifyDataSetChanged()
    }
}
