package com.example.bt1_29_10

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvError: TextView
    private lateinit var calendarView: CalendarView
    private var selectedDate: String = ""
    private var isCalendarVisible = false // Theo dõi trạng thái hiển thị của CalendarView
    private lateinit var btnToggleCalendar: Button
    private lateinit var addressHelper: AddressHelper
    private lateinit var provinceSpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private lateinit var wardSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ các thành phần giao diện
        provinceSpinner = findViewById(R.id.provinceSpinner)
        districtSpinner = findViewById(R.id.districtSpinner)
        wardSpinner = findViewById(R.id.wardSpinner)
        val etMssv = findViewById<EditText>(R.id.etMssv)
        val etName = findViewById<EditText>(R.id.etName)
        val rbMale = findViewById<RadioButton>(R.id.rbMale)
        val rbFemale = findViewById<RadioButton>(R.id.rbFemale)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val cbAgree = findViewById<CheckBox>(R.id.cbAgree)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        tvError = findViewById(R.id.tvError)
        btnToggleCalendar = findViewById(R.id.btnToggleCalendar)
        calendarView = findViewById(R.id.calendarView)

        calendarView.visibility = View.GONE

        // Khởi tạo AddressHelper và thiết lập Spinner địa chỉ
        addressHelper = AddressHelper(resources)
        setupProvinceSpinner()

        // Ẩn/hiện CalendarView khi nhấn nút "Chọn ngày sinh"
        btnToggleCalendar.setOnClickListener {
            calendarView.visibility = if (isCalendarVisible) View.GONE else View.VISIBLE
            isCalendarVisible = !isCalendarVisible
        }

        // Cập nhật text cho nút khi người dùng chọn ngày
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
            btnToggleCalendar.text = "Ngày sinh: $selectedDate"
            calendarView.visibility = View.GONE
            isCalendarVisible = false
        }

        // Xử lý sự kiện nút "Submit"
        btnSubmit.setOnClickListener {
            tvError.text = ""
            val mssv = etMssv.text.toString()
            val name = etName.text.toString()
            val gender = if (rbMale.isChecked) "Nam" else if (rbFemale.isChecked) "Nữ" else ""
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val interests = mutableListOf<String>()
            if (findViewById<CheckBox>(R.id.cbSports).isChecked) interests.add("Thể thao")
            if (findViewById<CheckBox>(R.id.cbMovies).isChecked) interests.add("Điện ảnh")
            if (findViewById<CheckBox>(R.id.cbMusic).isChecked) interests.add("Âm nhạc")

            // Kiểm tra các trường dữ liệu
            if (mssv.isEmpty() || name.isEmpty() || gender.isEmpty() || email.isEmpty() || phone.isEmpty()
                || selectedDate.isEmpty() || !cbAgree.isChecked) {
                tvError.text = "Vui lòng điền đầy đủ thông tin và đồng ý với điều khoản."
                return@setOnClickListener
            }

            // Nếu tất cả đều hợp lệ
            Toast.makeText(this, "Thông tin đã được gửi thành công!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupProvinceSpinner() {
        // Lấy danh sách tỉnh từ AddressHelper
        val provinces = addressHelper.getProvinces()
        val provinceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces)
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        provinceSpinner.adapter = provinceAdapter

        // Xử lý khi người dùng chọn tỉnh
        provinceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedProvince = provinces[position]
                setupDistrictSpinner(selectedProvince)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupDistrictSpinner(province: String) {
        // Lấy danh sách quận/huyện từ AddressHelper
        val districts = addressHelper.getDistricts(province)
        val districtAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districts)
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        districtSpinner.adapter = districtAdapter

        // Xử lý khi người dùng chọn quận/huyện
        districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedDistrict = districts[position]
                setupWardSpinner(province, selectedDistrict)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupWardSpinner(province: String, district: String) {
        // Lấy danh sách phường/xã từ AddressHelper
        val wards = addressHelper.getWards(province, district)
        val wardAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, wards)
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        wardSpinner.adapter = wardAdapter
    }
}
