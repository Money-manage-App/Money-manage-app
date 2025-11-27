package com.example.money_manage_app.data.local

import com.example.money_manage_app.data.local.entity.CategoryEntity

object DefaultCategories {

    // ✅ Template không có userId cụ thể
    private val expenseTemplate = listOf(
        CategoryEntity(nameVi = "Ăn uống", nameEn = "Food & Drink", iconName = "Restaurant", isExpense = true, displayOrder = 0, userId = ""),
        CategoryEntity(nameVi = "Mua sắm", nameEn = "Shopping", iconName = "ShoppingCart", isExpense = true, displayOrder = 1, userId = ""),
        CategoryEntity(nameVi = "Đi lại", nameEn = "Transportation", iconName = "DirectionsCar", isExpense = true, displayOrder = 2, userId = ""),
        CategoryEntity(nameVi = "Giải trí", nameEn = "Entertainment", iconName = "Movie", isExpense = true, displayOrder = 3, userId = ""),
        CategoryEntity(nameVi = "Xăng xe", nameEn = "Gasoline", iconName = "LocalGasStation", isExpense = true, displayOrder = 4, userId = ""),
        CategoryEntity(nameVi = "Quần áo", nameEn = "Clothing", iconName = "Checkroom", isExpense = true, displayOrder = 5, userId = ""),
        CategoryEntity(nameVi = "Nhà cửa", nameEn = "Housing", iconName = "Home", isExpense = true, displayOrder = 6, userId = ""),
        CategoryEntity(nameVi = "Hóa đơn", nameEn = "Bills", iconName = "Receipt", isExpense = true, displayOrder = 7, userId = ""),
        CategoryEntity(nameVi = "Thể thao", nameEn = "Sports", iconName = "FitnessCenter", isExpense = true, displayOrder = 8, userId = ""),
        CategoryEntity(nameVi = "Sức khỏe", nameEn = "Health", iconName = "Favorite", isExpense = true, displayOrder = 9, userId = ""),
        CategoryEntity(nameVi = "Sửa chữa", nameEn = "Repairs", iconName = "Build", isExpense = true, displayOrder = 10, userId = ""),
        CategoryEntity(nameVi = "Bảo hiểm", nameEn = "Insurance", iconName = "Security", isExpense = true, displayOrder = 11, userId = ""),
        CategoryEntity(nameVi = "Quà tặng", nameEn = "Gifts", iconName = "CardGiftcard", isExpense = true, displayOrder = 12, userId = ""),
        CategoryEntity(nameVi = "Thú cưng", nameEn = "Pets", iconName = "Pets", isExpense = true, displayOrder = 13, userId = ""),
        CategoryEntity(nameVi = "Game", nameEn = "Gaming", iconName = "SportsEsports", isExpense = true, displayOrder = 14, userId = ""),
        CategoryEntity(nameVi = "Bóng đá", nameEn = "Football", iconName = "SportsSoccer", isExpense = true, displayOrder = 15, userId = "")
    )

    private val incomeTemplate = listOf(
        CategoryEntity(nameVi = "Lương", nameEn = "Salary", iconName = "AttachMoney", isExpense = false, displayOrder = 0, userId = ""),
        CategoryEntity(nameVi = "Thưởng", nameEn = "Bonus", iconName = "Star", isExpense = false, displayOrder = 1, userId = ""),
        CategoryEntity(nameVi = "Đầu tư", nameEn = "Investment", iconName = "TrendingUp", isExpense = false, displayOrder = 2, userId = ""),
        CategoryEntity(nameVi = "Tiền lãi", nameEn = "Interest", iconName = "AccountBalance", isExpense = false, displayOrder = 3, userId = ""),
        CategoryEntity(nameVi = "Tiết kiệm", nameEn = "Savings", iconName = "Savings", isExpense = false, displayOrder = 4, userId = ""),
        CategoryEntity(nameVi = "Làm thêm", nameEn = "Part-time", iconName = "Work", isExpense = false, displayOrder = 5, userId = ""),
        CategoryEntity(nameVi = "Giải thưởng", nameEn = "Prize", iconName = "MilitaryTech", isExpense = false, displayOrder = 6, userId = ""),
        CategoryEntity(nameVi = "Quà tặng", nameEn = "Gift", iconName = "Favorite", isExpense = false, displayOrder = 7, userId = ""),
        CategoryEntity(nameVi = "Bất ngờ", nameEn = "Surprise", iconName = "AutoAwesome", isExpense = false, displayOrder = 8, userId = ""),
        CategoryEntity(nameVi = "Thu nhập khác", nameEn = "Other Income", iconName = "Money", isExpense = false, displayOrder = 9, userId = "")
    )

    // ✅ Method chính: Tạo categories cho user cụ thể
    fun createDefaultCategoriesForUser(userId: String): List<CategoryEntity> {
        return (expenseTemplate + incomeTemplate).map { it.copy(userId = userId) }
    }

    // ✅ Giữ lại để backward compatible (dùng cho guest)
    val expenseCategories: List<CategoryEntity>
        get() = expenseTemplate.map { it.copy(userId = "guest") }

    val incomeCategories: List<CategoryEntity>
        get() = incomeTemplate.map { it.copy(userId = "guest") }
}