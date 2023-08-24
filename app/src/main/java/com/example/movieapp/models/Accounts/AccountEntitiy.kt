import androidx.room.Entity

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val password: String
) {
    annotation class PrimaryKey(val autoGenerate: Boolean)
}
