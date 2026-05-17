package com.jalSanchay.tracker.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class Tip(
    val id: Int,
    val title: String,
    val summary: String,
    val detail: String,
    val category: String,
    val difficulty: String,
    val timeEstimate: String
)

@HiltViewModel
class TipsViewModel @Inject constructor() : ViewModel() {

    private val allTips = listOf(
        Tip(
            id = 1,
            title = "First Flush Diverter",
            summary = "Divert the first rain to remove contaminants from your roof.",
            detail = "A first flush diverter captures the initial rainwater runoff from your roof, which contains the highest concentration of pollutants like dust, bird droppings, and leaves. Install a simple PVC pipe diverter at the downpipe junction. The first 1-2 liters per square meter of roof area should be diverted. This significantly improves water quality for storage. Use a ball-valve or floating ball mechanism for automatic operation. Clean the diverter chamber after each rain event for best results.",
            category = "Setup",
            difficulty = "Medium",
            timeEstimate = "2-3 hours"
        ),
        Tip(
            id = 2,
            title = "Gutter Maintenance",
            summary = "Keep gutters clean to maximize water collection efficiency.",
            detail = "Clean gutters at least twice a year — before and after monsoon season. Remove leaves, debris, and sediment. Check for rust, holes, and proper slope (minimum 1% gradient). Use gutter guards or mesh screens to prevent clogging. Seal any leaks with waterproof silicone. Ensure downpipes are clear and connected properly to your storage tank. Tools needed: ladder, garden hose, gutter scoop, wire brush, and sealant. Regular maintenance can improve collection efficiency by up to 20%.",
            category = "Maintenance",
            difficulty = "Easy",
            timeEstimate = "1-2 hours"
        ),
        Tip(
            id = 3,
            title = "Roof Material Impact",
            summary = "Different roofing materials have different water collection efficiencies.",
            detail = "Runoff coefficients by material:\n• Metal/GI Sheet: 0.90 — Best for harvesting, smooth surface\n• Concrete/Tile: 0.85 — Good efficiency, common in urban areas\n• Asbestos: 0.80 — Decent but health concerns with old sheets\n• Thatch/Grass: 0.40 — Absorbs most water, least efficient\n\nConsider coating rough surfaces with food-safe sealant to improve runoff. Metal roofs also have the advantage of cooling water slightly during collection.",
            category = "Setup",
            difficulty = "Easy",
            timeEstimate = "15 minutes read"
        ),
        Tip(
            id = 4,
            title = "Monsoon Preparation",
            summary = "Get your harvesting system ready before the rains arrive.",
            detail = "Pre-monsoon checklist:\n• Inspect and clean all gutters and downpipes\n• Check tank for cracks, leaks, and structural integrity\n• Clean or replace filters (mesh, sand, charcoal)\n• Test first flush diverter mechanism\n• Empty and clean storage tank\n• Check overflow pipe direction and drainage\n• Ensure mosquito mesh is intact on all openings\n• Calibrate your rain gauge\n• Stock up on water testing kits\n• Review and update your Jal-Sanchay setup if roof area changed",
            category = "Monsoon",
            difficulty = "Medium",
            timeEstimate = "Half day"
        ),
        Tip(
            id = 5,
            title = "Tank Cleaning",
            summary = "Regular tank cleaning ensures safe and quality water storage.",
            detail = "Step-by-step tank cleaning guide:\n1. Drain the tank completely using the bottom outlet\n2. Remove sediment from the bottom using a bucket and brush\n3. Scrub interior walls with a stiff brush (no chemical detergents)\n4. Use a mixture of water and white vinegar for disinfection\n5. Rinse thoroughly with clean water at least twice\n6. Check for cracks, algae growth, and structural damage\n7. Ensure inlet and outlet pipes are clear\n8. Refill and let stand for 24 hours before use\n\nClean your tank at least twice a year, preferably before monsoon season.",
            category = "Maintenance",
            difficulty = "Medium",
            timeEstimate = "3-4 hours"
        ),
        Tip(
            id = 6,
            title = "Overflow Management",
            summary = "Direct overflow water to gardens or groundwater recharge pits.",
            detail = "When your tank is full, overflow water should not be wasted. Options:\n\n1. Garden irrigation: Connect overflow pipe to a drip irrigation system or garden bed\n2. Groundwater recharge: Direct overflow to a recharge pit (1m x 1m x 2m deep, filled with gravel and sand layers)\n3. Secondary storage: Add a smaller overflow tank for non-potable uses\n4. Rain garden: Create a shallow depression with native plants that can absorb excess water\n\nAlways ensure overflow exits away from building foundations to prevent structural damage.",
            category = "Setup",
            difficulty = "Advanced",
            timeEstimate = "1-2 days"
        ),
        Tip(
            id = 7,
            title = "Filter Types",
            summary = "Choose the right filter for your rainwater harvesting system.",
            detail = "Comparison of filter types:\n\n• Mesh Filter (Easy): Simple wire mesh at inlet. Removes leaves and large debris. Clean after every rain. Cost: ₹200-500\n\n• Sand Filter (Medium): Layers of sand and gravel. Removes fine particles and some bacteria. Needs periodic backwashing. Cost: ₹2000-5000\n\n• Charcoal Filter (Medium): Activated carbon removes odor, color, and chemicals. Replace charcoal every 6 months. Cost: ₹1000-3000\n\n• UV Filter (Advanced): Ultraviolet light kills bacteria and viruses. Requires electricity. Best for drinking water. Cost: ₹5000-15000\n\nFor most home systems, a combination of mesh + sand filter provides excellent results.",
            category = "Setup",
            difficulty = "Medium",
            timeEstimate = "Varies"
        ),
        Tip(
            id = 8,
            title = "Water Quality Testing",
            summary = "Simple tests to ensure your harvested rainwater is safe.",
            detail = "DIY water quality tests:\n\n1. Visual Test: Water should be clear, not cloudy or colored\n2. Smell Test: No unusual odor indicates good quality\n3. pH Test: Use pH strips (ideal range: 6.5-8.5). Available at pharmacies for ₹100-200\n4. TDS Test: Use a TDS meter (ideal: below 300 ppm for drinking). Cost: ₹300-500\n5. Bacterial Test: Use home testing kits for coliform bacteria. Test monthly during monsoon\n\nWhen to test:\n• After first rain of season\n• Monthly during regular use\n• After tank cleaning\n• If water appears, smells, or tastes different\n\nIf water fails any test, clean filters and tank before using.",
            category = "Conservation",
            difficulty = "Easy",
            timeEstimate = "30 minutes"
        ),
        Tip(
            id = 9,
            title = "Maximizing Roof Collection",
            summary = "Optimize gutter placement for maximum water capture.",
            detail = "To maximize collection:\n\n1. Calculate your roof area accurately (length × width for each section)\n2. Place gutters along ALL roof edges that drain toward your tank\n3. Ensure minimum gutter slope of 1cm per meter toward downpipe\n4. Size gutters appropriately: 100mm half-round for up to 50m² roof area, 125mm for larger\n5. Use splash guards at valley points where two roof sections meet\n6. Position downpipes at lowest points\n7. Minimize distance between downpipe and tank to reduce losses\n8. Consider adding multiple downpipes for large roof areas\n\nA well-designed gutter system can capture 95%+ of available roof runoff.",
            category = "DIY",
            difficulty = "Advanced",
            timeEstimate = "1 day"
        ),
        Tip(
            id = 10,
            title = "Grey Water Reuse",
            summary = "Reuse household water for gardens and flushing.",
            detail = "Grey water (from sinks, showers, washing machines) can supplement your rainwater:\n\n• Legal: Most Indian states allow grey water reuse for non-potable purposes. Check local municipal guidelines\n• Garden use: Grey water with biodegradable soap is safe for plants. Avoid using water with bleach or harsh chemicals\n• Toilet flushing: Grey water can be stored in a separate tank for flushing\n• Treatment: Simple grease trap + sand filter makes grey water suitable for garden use\n• Volume: Average household produces 80-100 liters of reusable grey water daily\n\nCombining rainwater harvesting with grey water reuse can reduce municipal water dependence by 40-60%.",
            category = "Conservation",
            difficulty = "Medium",
            timeEstimate = "Ongoing"
        )
    )

    private val _tips = MutableStateFlow(allTips)
    val tips: StateFlow<List<Tip>> = _tips.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _bookmarkedIds = MutableStateFlow<Set<Int>>(emptySet())
    val bookmarkedIds: StateFlow<Set<Int>> = _bookmarkedIds.asStateFlow()

    val categories = listOf("All", "Setup", "Maintenance", "Monsoon", "Conservation", "DIY")

    fun search(query: String) {
        _searchQuery.value = query
        filterTips()
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        filterTips()
    }

    private fun filterTips() {
        val query = _searchQuery.value.lowercase()
        val category = _selectedCategory.value
        _tips.value = allTips.filter { tip ->
            val matchesSearch = query.isEmpty() || tip.title.lowercase().contains(query) ||
                    tip.summary.lowercase().contains(query) || tip.detail.lowercase().contains(query)
            val matchesCategory = category == "All" || tip.category == category
            matchesSearch && matchesCategory
        }
    }

    fun toggleBookmark(tipId: Int) {
        val current = _bookmarkedIds.value.toMutableSet()
        if (current.contains(tipId)) current.remove(tipId) else current.add(tipId)
        _bookmarkedIds.value = current
    }

    fun getBookmarkedTips(): List<Tip> {
        return allTips.filter { _bookmarkedIds.value.contains(it.id) }
    }
}
