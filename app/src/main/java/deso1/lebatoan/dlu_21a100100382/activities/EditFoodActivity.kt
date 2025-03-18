package deso1.lebatoan.dlu_21a100100382.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import deso1.lebatoan.dlu_21a100100382.R
import deso1.lebatoan.dlu_21a100100382.databinding.ActivityEditFoodBinding

class EditFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditFoodBinding
    private var selectedImageUri: Uri? = null
    private var isEditMode = false

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.editFoodImage.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if we're in edit mode
        isEditMode = intent.hasExtra("foodId")

        // Setup UI based on mode
        if (isEditMode) {
            binding.editFoodName.setText(intent.getStringExtra("foodName"))
            binding.editFoodPrice.setText(intent.getDoubleExtra("foodPrice", 0.0).toString())
            binding.editFoodUnit.setText(intent.getStringExtra("foodUnit"))
            binding.deleteButton.visibility = View.VISIBLE

            val imageUri = intent.getStringExtra("foodImage") ?: ""
            if (imageUri.startsWith("content://")) {
                binding.editFoodImage.setImageURI(Uri.parse(imageUri))
                selectedImageUri = Uri.parse(imageUri)
            } else {
                selectedImageUri = null
                val resourceId = resources.getIdentifier(
                    imageUri,
                    "drawable",
                    packageName
                )
                if (resourceId != 0) {
                    binding.editFoodImage.setImageResource(resourceId)
                }
            }
        } else {
            binding.deleteButton.visibility = View.GONE
        }

        binding.editFoodImage.setOnClickListener {
            checkAndRequestPermissions()
        }

        binding.saveButton.setOnClickListener {
            val name = binding.editFoodName.text.toString()
            val priceStr = binding.editFoodPrice.text.toString()
            val unit = binding.editFoodUnit.text.toString()

            if (name.isEmpty() || priceStr.isEmpty() || unit.isEmpty()) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull()
            if (price == null) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val foodImage = selectedImageUri?.toString() ?: (intent.getStringExtra("foodImage") ?: "pho_bo")

            val resultIntent = Intent().apply {
                putExtra("foodId", if (isEditMode) intent.getStringExtra("foodId") else System.currentTimeMillis().toString())
                putExtra("foodName", name)
                putExtra("foodPrice", price)
                putExtra("foodUnit", unit)
                putExtra("foodImage", foodImage)
            }
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.deleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete) { _, _ ->
                    val resultIntent = Intent().apply {
                        putExtra("foodId", intent.getStringExtra("foodId"))
                        putExtra("shouldDelete", true)
                    }
                    setResult(RESULT_OK, resultIntent)
                    Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                openImagePicker()
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                openImagePicker()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
