import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rainfall_table")
data class RainfallEntry(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val date: Long,
    val rainfallMm: Double,
    val litersSaved: Double
)
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RainfallDao {
    @Insert
    suspend fun insertRainfall(entry: RainfallEntry)

    @Query("SELECT * FROM rainfall_table ORDER BY date DESC")
    suspend fun getAllRainfall(): List<RainfallEntry>

    @Query("SELECT SUM(litersSaved) FROM rainfall_table")
    suspend fun getTotalLiters(): Double?
}
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RainfallEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rainfallDao(): RainfallDao
}
object WaterCalculator {
    fun calculateLiters(areaSqFt: Double, rainfallMm: Double, runoffCoeff: Double = 0.8): Double {
        if (areaSqFt < 0 || rainfallMm < 0 || runoffCoeff < 0) return 0.0
        return areaSqFt * rainfallMm * 0.0929 * runoffCoeff
    }

    fun getImpactScore(totalLiters: Double, dailyUsage: Double = 135.0): Double {
        if (dailyUsage <= 0) return 0.0
        return totalLiters / dailyUsage
    }
}
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SetupScreen(onSave: (Double, Double) -> Unit) {
    var roofArea by remember { mutableStateOf("") }
    var tankCapacity by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Setup Your Harvesting System", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = roofArea,
            onValueChange = { roofArea = it },
            label = { Text("Roof Area (Sq Ft)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = tankCapacity,
            onValueChange = { tankCapacity = it },
            label = { Text("Tank Capacity (Liters)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { 
                onSave(roofArea.toDoubleOrNull() ?: 0.0, tankCapacity.toDoubleOrNull() ?: 0.0) 
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Settings")
        }
    }
}
@Composable
fun DataEntryScreen(onCalculate: (Double) -> Unit) {
    var rainfallInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Log New Rainfall", style = MaterialTheme.typography.headlineSmall)
        
        OutlinedTextField(
            value = rainfallInput,
            onValueChange = { rainfallInput = it },
            label = { Text("Rainfall Amount (mm)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = { onCalculate(rainfallInput.toDoubleOrNull() ?: 0.0) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Calculate & Save")
        }
    }
}
@Composable
fun DashboardScreen(totalSaved: Double, impactScore: Double, tankFillLevel: Float) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Water Wealth Dashboard", style = MaterialTheme.typography.headlineMedium)
        
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Liters Saved: ${totalSaved.toInt()} L")
                Text("Household Usage Days: ${String.format("%.1f", impactScore)}")
            }
        }
        
        Text("Tank Level", style = MaterialTheme.typography.bodyLarge)
        LinearProgressIndicator(
            progress = tankFillLevel,
            modifier = Modifier.fillMaxWidth().height(20.dp)
        )
    }
}

