package edu.camilacanete.ej07p03

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import edu.camilacanete.ej07p03.databinding.ActivityMainBinding
import edu.camilacanete.ej07p03.ui.fragment.CalculatorIMCFragment
import edu.camilacanete.ej07p03.ui.fragment.HistoricalFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val viewPager2 = binding.viewPager
        // Se crea un adaptador para el ViewPager2 y se añaden los fragmentos para cada pestaña
        val adapter = ViewPager2Adapter(supportFragmentManager, lifecycle).apply {
            addFragment(CalculatorIMCFragment(),"IMC")
            addFragment(HistoricalFragment(),"Histórico")
        }

        viewPager2.adapter = adapter
        viewPager2.setPageTransformer(MarginPageTransformer(1500)) // efecto de desplazamiento

        // Se asocia el TabLayout con el ViewPager2
        TabLayoutMediator(binding.tabLayout,viewPager2) { tab, position ->
            tab.text = adapter.getPageTitle(position)
            tab.contentDescription = adapter.getPageTitle(position)
        }.attach()
    }
}