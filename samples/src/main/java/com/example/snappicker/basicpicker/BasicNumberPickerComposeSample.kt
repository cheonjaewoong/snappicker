package com.example.snappicker.basicpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.snappicker.databinding.FragmentBasicNumberPickerComposeSampleBinding
import io.woong.snappicker.compose.ExperimentalSnapPickerApi
import io.woong.snappicker.compose.VerticalSnapPicker

public class BasicNumberPickerComposeSample : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBasicNumberPickerComposeSampleBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener {}
        binding.composeView.setContent {
            SampleContent()
        }
        return binding.root
    }
}

@OptIn(ExperimentalSnapPickerApi::class)
@Composable
private fun SampleContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xfff9f9f9))
            .padding(16.dp)
    ) {
        VerticalSnapPicker(
            values = (1..100).toList(),
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            itemContent = { value ->
                PickerItem(text = value.toString())
            }
        )
    }
}

@Composable
private fun PickerItem(text: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        BasicText(text = text, modifier = Modifier.align(Alignment.Center))
    }
}
