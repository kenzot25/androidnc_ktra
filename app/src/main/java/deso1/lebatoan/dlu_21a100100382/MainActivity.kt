package deso1.lebatoan.dlu_21a100100382

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import deso1.lebatoan.dlu_21a100100382.activities.EditFoodActivity
import deso1.lebatoan.dlu_21a100100382.adapters.FoodAdapter
import deso1.lebatoan.dlu_21a100100382.databinding.ActivityMainBinding
import deso1.lebatoan.dlu_21a100100382.models.Food

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var foodAdapter: FoodAdapter
    private val foodList = mutableListOf<Food>()

    private val editFoodLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { intent ->
                    val action = intent.getStringExtra("action")
                    val foodId = intent.getStringExtra("foodId") ?: return@let

                    if (action == "delete") {
                        foodList.removeAll { it.id == foodId }
                    } else {
                        val foodName = intent.getStringExtra("foodName") ?: ""
                        val foodPrice = intent.getDoubleExtra("foodPrice", 0.0)
                        val foodUnit = intent.getStringExtra("foodUnit") ?: ""
                        val foodImage = intent.getStringExtra("foodImage") ?: ""

                        val index = foodList.indexOfFirst { it.id == foodId }
                        if (index != -1) {
                            foodList[index] = Food(foodId, foodName, foodPrice, foodUnit, foodImage)
                        } else {
                            foodList.add(Food(foodId, foodName, foodPrice, foodUnit, foodImage))
                        }
                    }
                    foodAdapter.updateData(foodList)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupAddButton()
        loadSampleData()
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(foodList) { food ->
            val intent = Intent(this, EditFoodActivity::class.java).apply {
                putExtra("foodId", food.id)
                putExtra("foodName", food.name)
                putExtra("foodPrice", food.price)
                putExtra("foodUnit", food.unit)
                putExtra("foodImage", food.imageUrl)
            }
            editFoodLauncher.launch(intent)
        }
        binding.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = foodAdapter
        }
    }

    private fun setupAddButton() {
        binding.addFoodFab.setOnClickListener {
            val intent = Intent(this, EditFoodActivity::class.java)
            editFoodLauncher.launch(intent)
        }
    }

    private fun loadSampleData() {
        foodList.addAll(Food.getSampleData())
        foodAdapter.updateData(foodList)
    }
}
