package com.example.money_manage_app.data.local

import com.example.money_manage_app.data.local.entity.CategoryEntity

object DefaultCategories {

    // ✅ THÊM displayOrder cho tất cả default categories
    val expenseCategories = listOf(
        CategoryEntity(
            nameVi = "Mua sắm",
            nameEn = "Shopping",
            iconName = "ShoppingCart",
            isExpense = true,
            displayOrder = 0
        ),
        CategoryEntity(
            nameVi = "Giải trí",
            nameEn = "Entertainment",
            iconName = "SportsEsports",
            isExpense = true,
            displayOrder = 1
        ),
        CategoryEntity(
            nameVi = "Quần áo",
            nameEn = "Clothes",
            iconName = "Checkroom",
            isExpense = true,
            displayOrder = 2
        ),
        CategoryEntity(
            nameVi = "Thú cưng",
            nameEn = "Pets",
            iconName = "Pets",
            isExpense = true,
            displayOrder = 3
        ),
        CategoryEntity(
            nameVi = "Đồ ăn",
            nameEn = "Food",
            iconName = "Restaurant",
            isExpense = true,
            displayOrder = 4
        ),
        CategoryEntity(
            nameVi = "Thể thao",
            nameEn = "Sports",
            iconName = "SportsSoccer",
            isExpense = true,
            displayOrder = 5
        ),
        CategoryEntity(
            nameVi = "Sức khỏe",
            nameEn = "Health",
            iconName = "Favorite",
            isExpense = true,
            displayOrder = 6
        ),
        CategoryEntity(
            nameVi = "Sửa chữa",
            nameEn = "Repair",
            iconName = "Build",
            isExpense = true,
            displayOrder = 7
        ),
        CategoryEntity(
            nameVi = "Biếu tặng",
            nameEn = "Gift",
            iconName = "CardGiftcard",
            isExpense = true,
            displayOrder = 8
        )
    )

    val incomeCategories = listOf(
        CategoryEntity(
            nameVi = "Lương",
            nameEn = "Salary",
            iconName = "Money",
            isExpense = false,
            displayOrder = 0
        ),
        CategoryEntity(
            nameVi = "Khoản đầu tư",
            nameEn = "Investment",
            iconName = "Savings",
            isExpense = false,
            displayOrder = 1
        ),
        CategoryEntity(
            nameVi = "Bán thời gian",
            nameEn = "Part-time",
            iconName = "Schedule",
            isExpense = false,
            displayOrder = 2
        ),
        CategoryEntity(
            nameVi = "Khác",
            nameEn = "Other",
            iconName = "MoreHoriz",
            isExpense = false,
            displayOrder = 3
        )
    )
}