package com.example.labirynt

        import android.content.Intent
        import android.os.Bundle
        import android.view.LayoutInflater
        import android.view.View
        import android.view.ViewGroup
        import androidx.fragment.app.Fragment
        import com.example.labirynt.databinding.ActivityPauseBinding

        class PauseFragment : Fragment() {

            private var _binding: ActivityPauseBinding? = null
            private val binding get() = _binding!!

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View {
                _binding = ActivityPauseBinding.inflate(inflater, container, false)
                return binding.root
            }

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                binding.resumeButton.setOnClickListener {
                    activity?.findViewById<View>(R.id.pauseFragmentContainer)?.visibility = View.GONE
                    parentFragmentManager.beginTransaction()
                        .remove(this)
                        .commit()
                }

                binding.mainMenuButton.setOnClickListener {
                    val intent = Intent(activity, StartActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    activity?.finish()
                }

                binding.exitButton.setOnClickListener {
                    activity?.finishAffinity()
                }
            }

            override fun onDestroyView() {
                super.onDestroyView()
                _binding = null
            }
        }