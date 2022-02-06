package co.ke.xently.data

import androidx.room.*
import co.ke.xently.common.Exclude
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(
    tableName = "products",
    indices = [
        Index("shopId"),
    ],
)
data class Product(
    @PrimaryKey(autoGenerate = false) var id: Long = -1L,
    var name: String = "",
    var unit: String = "",
    var unitQuantity: Float = 0f,
    @Exclude
    @Ignore
    var shop: Shop = Shop.default(),
    @SerializedName("shop")
    var shopId: Long = -1L,
    var unitPrice: Float = 0f,
    var datePurchased: Date = Date(),
    var dateAdded: Date = Date(),
    var isDefault: Boolean = false,
) {
    data class WithShop(
        @Embedded
        val product: Product,
        @Relation(parentColumn = "shopId", entityColumn = "id")
        val shop: Shop?,
    )

    override fun toString(): String {
        return "${name}, $unitQuantity $unit"
    }

    companion object {
        fun default(): Product = Product(isDefault = true)
    }
}
