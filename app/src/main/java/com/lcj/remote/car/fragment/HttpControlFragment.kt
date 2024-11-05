package com.lcj.remote.car.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.lcj.remote.car.R
import com.lcj.remote.car.databinding.FragmentControlBinding
import com.lcj.remote.car.model.ControlResponse
import com.lcj.remote.car.network.RemoteCarAPI
import io.reactivex.android.schedulers.AndroidSchedulers

class HttpControlFragment : Fragment(R.layout.fragment_control) {
    private val LOG_TAG = javaClass.simpleName

    private lateinit var binding: FragmentControlBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentControlBinding.bind(view!!)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.serverIpEdit) {
//            setText(NetworkUtils.getGatewayIp(requireContext()))
            setText("192.168.12.1")
        }

        with(binding.forwardButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "forwardButton")
                sendMotorCommand("direction", 1)
            }
        }

        with(binding.backwardButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "backwardButton")
                sendMotorCommand("direction", 2)
            }
        }

        with(binding.acceleratorButton) {
            isEnabled = false
            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        Log.i(LOG_TAG, "acceleratorButton ACTION_UP")
                        sendMotorCommand("accelertor", 0)
                        true
                    }

                    MotionEvent.ACTION_DOWN -> {
                        Log.i(LOG_TAG, "acceleratorButton ACTION_DOWN")
                        sendMotorCommand("accelertor", 1)
                        true
                    }
                }
                return@setOnTouchListener false
            }
        }

        with(binding.frontLightOnButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "front_light")
                sendLightCommand("front_light", 1)
            }
        }

        with(binding.backLightOnButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "back_light")
                sendLightCommand("back_light", 1)
            }
        }

        with(binding.lightLeftBlinkButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "turn_left")
                sendLightCommand("turn_left", 1)
            }
        }

        with(binding.lightRightBlinkButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "turn_right")
                sendLightCommand("turn_right", 1)
            }
        }

        with(binding.lightOnButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "all on")
                sendLightCommand("all", 1)
            }
        }

        with(binding.lightBlinkButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "parking")
                sendLightCommand("parking", 1)
            }
        }

        with(binding.lightOffButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "all off")
                sendLightCommand("all", 0)
            }
        }

        with(binding.musicButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "musicButton")
                sendSoundCommand("music", 1)
            }
        }

        with(binding.hornButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "hornButton")
                sendSoundCommand("horn", 1)
            }
        }

        with(binding.disconnectButton) {
            isEnabled = false
            isVisible = false
            setOnClickListener {
            }
        }

        with(binding.connectButton) {
            setOnClickListener {
                checkService()
            }
        }
    }

    private fun updateView(response: ControlResponse) {
        val direction = response.direction
        val light = response.light

        when (direction) {
            1 -> {
                binding.forwardButton.isEnabled = false
                binding.backwardButton.isEnabled = true
            }

            2 -> {
                binding.forwardButton.isEnabled = true
                binding.backwardButton.isEnabled = false
            }
        }

        when (light) {
            0 -> {
                binding.frontLightOnButton.isEnabled = true
                binding.backLightOnButton.isEnabled = true
                binding.lightLeftBlinkButton.isEnabled = true
                binding.lightRightBlinkButton.isEnabled = true
                binding.lightOnButton.isEnabled = true
                binding.lightBlinkButton.isEnabled = true
                binding.lightOffButton.isEnabled = false
            }

            1 -> {
                binding.frontLightOnButton.isEnabled = false
                binding.backLightOnButton.isEnabled = true
                binding.lightLeftBlinkButton.isEnabled = true
                binding.lightRightBlinkButton.isEnabled = true
                binding.lightOnButton.isEnabled = true
                binding.lightBlinkButton.isEnabled = true
                binding.lightOffButton.isEnabled = true
            }

            2 -> {
                binding.frontLightOnButton.isEnabled = true
                binding.backLightOnButton.isEnabled = false
                binding.lightLeftBlinkButton.isEnabled = true
                binding.lightRightBlinkButton.isEnabled = true
                binding.lightOnButton.isEnabled = true
                binding.lightBlinkButton.isEnabled = true
                binding.lightOffButton.isEnabled = true
            }

            3 -> {
                binding.frontLightOnButton.isEnabled = true
                binding.backLightOnButton.isEnabled = true
                binding.lightLeftBlinkButton.isEnabled = true
                binding.lightRightBlinkButton.isEnabled = true
                binding.lightOnButton.isEnabled = false
                binding.lightBlinkButton.isEnabled = true
                binding.lightOffButton.isEnabled = true
            }

            4 -> {
                binding.frontLightOnButton.isEnabled = true
                binding.backLightOnButton.isEnabled = true
                binding.lightLeftBlinkButton.isEnabled = false
                binding.lightRightBlinkButton.isEnabled = true
                binding.lightOnButton.isEnabled = true
                binding.lightBlinkButton.isEnabled = true
                binding.lightOffButton.isEnabled = true
            }

            5 -> {
                binding.frontLightOnButton.isEnabled = true
                binding.backLightOnButton.isEnabled = true
                binding.lightLeftBlinkButton.isEnabled = true
                binding.lightRightBlinkButton.isEnabled = false
                binding.lightOnButton.isEnabled = true
                binding.lightBlinkButton.isEnabled = true
                binding.lightOffButton.isEnabled = true
            }

            6 -> {
                binding.frontLightOnButton.isEnabled = true
                binding.backLightOnButton.isEnabled = true
                binding.lightLeftBlinkButton.isEnabled = true
                binding.lightRightBlinkButton.isEnabled = true
                binding.lightOnButton.isEnabled = true
                binding.lightBlinkButton.isEnabled = false
                binding.lightOffButton.isEnabled = true
            }
        }

        binding.acceleratorButton.isEnabled = true
        binding.musicButton.isEnabled = true
        binding.hornButton.isEnabled = true
    }

    private fun checkService() {
        val serverIP = binding.serverIpEdit.text.toString()
        RemoteCarAPI.init(requireContext(), serverIP)

        RemoteCarAPI.status()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d(LOG_TAG, "response: $response")
                updateView(response)
            }, { e ->
                e.printStackTrace()
            }).let { }
    }

    private fun sendMotorCommand(cmd: String, value: Int) {
        RemoteCarAPI.motor(cmd, value)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d(LOG_TAG, "response: $response")
                updateView(response)
            }, { e ->
                e.printStackTrace()
            }).let { }
    }

    private fun sendLightCommand(cmd: String, value: Int) {
        RemoteCarAPI.light(cmd, value)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d(LOG_TAG, "response: $response")
                updateView(response)
            }, { e ->
                e.printStackTrace()
            }).let { }
    }

    private fun sendSoundCommand(cmd: String, value: Int) {
        RemoteCarAPI.sound(cmd, value)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d(LOG_TAG, "response: $response")
                updateView(response)
            }, { e ->
                e.printStackTrace()
            }).let { }
    }
}