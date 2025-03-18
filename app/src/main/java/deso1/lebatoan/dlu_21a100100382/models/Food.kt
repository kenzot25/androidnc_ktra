package deso1.lebatoan.dlu_21a100100382.models

data class Food(
    val id: String,
    var name: String,
    var price: Double,
    var unit: String,
    var imageUrl: String
) {
    companion object {
        fun getSampleData(): List<Food> = listOf(
            Food(
                "1",
                "Phở bò",
                100000.0,
                "tô",
                "pho_bo"
            ),
            Food(
                "2",
                "Cơm tấm",
                45000.0,
                "đĩa",
                "com_tam"
            ),
            Food(
                "3",
                "Bún chả",
                35000.0,
                "phần",
                "bun_cha"
            )
        )
    }
}
