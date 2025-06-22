package com.example.maytinhcamtay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // Khai báo các thành phần giao diện
    TextView tvHistory, tvResult;
    Button bt0, bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9, btPhay, btCong, btTru, btNhan, btChia, btAC, btC, btBang, btAmDuong, btAns;
    // Nút nâng cao (chỉ dùng ở chế độ ngang)
    Button btBinhPhuong, btXmuY, btCanBacHai, btln, btlogXT, btCanBacYCuaX, btArcSin, btArcCos, btArcTan, btSin, btCos, btTan, btGiaiThua, btE, btPi;

    // Biến logic
    private String number = null;
    double lastnumber = 0;
    double firstnumber = 0;
    double ans = 0; // Lưu kết quả trước đó
    String status = null;
    boolean operator = false;
    boolean dot = true;
    boolean ACcontrol = true;
    boolean equal = false;
    boolean isEqual = false;
    boolean waitingForSecondNumber = false;
    boolean isBangLocked = false; // Biến khóa nút bằng

    // Định dạng số
    String pattern = "###,###.#####";
    DecimalFormat decimalFormat = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.US));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khôi phục trạng thái nếu có
        if (savedInstanceState != null) {
            number = savedInstanceState.getString("NUMBER");
            ans = savedInstanceState.getDouble("ANS");
            firstnumber = savedInstanceState.getDouble("FIRST_NUMBER");
            lastnumber = savedInstanceState.getDouble("LAST_NUMBER");
            status = savedInstanceState.getString("STATUS");
            operator = savedInstanceState.getBoolean("OPERATOR");
            dot = savedInstanceState.getBoolean("DOT");
            ACcontrol = savedInstanceState.getBoolean("AC_CONTROL");
            equal = savedInstanceState.getBoolean("EQUAL");
            isEqual = savedInstanceState.getBoolean("IS_EQUAL");
            waitingForSecondNumber = savedInstanceState.getBoolean("WAITING_FOR_SECOND_NUMBER");
            isBangLocked = savedInstanceState.getBoolean("IS_BANG_LOCKED");
        }

        // Ánh xạ giao diện
        try {
            tvHistory = findViewById(R.id.tvHistory);
            tvResult = findViewById(R.id.tvResult);
            bt0 = findViewById(R.id.bt0);
            bt1 = findViewById(R.id.bt1);
            bt2 = findViewById(R.id.bt2);
            bt3 = findViewById(R.id.bt3);
            bt4 = findViewById(R.id.bt4);
            bt5 = findViewById(R.id.bt5);
            bt6 = findViewById(R.id.bt6);
            bt7 = findViewById(R.id.bt7);
            bt8 = findViewById(R.id.bt8);
            bt9 = findViewById(R.id.bt9);
            btPhay = findViewById(R.id.btPhay);
            btCong = findViewById(R.id.btCong);
            btTru = findViewById(R.id.btTru);
            btNhan = findViewById(R.id.btNhan);
            btChia = findViewById(R.id.btChia);
            btAC = findViewById(R.id.btAC);
            btC = findViewById(R.id.btC);
            btBang = findViewById(R.id.btBang);
            btAmDuong = findViewById(R.id.btAmDuong);
            btAns = findViewById(R.id.btAns);

            // Khôi phục giao diện
            tvResult.setText(savedInstanceState != null ? savedInstanceState.getString("RESULT_TEXT", "0") : "0");
            tvHistory.setText(savedInstanceState != null ? savedInstanceState.getString("HISTORY_TEXT", "") : "");
            btC.setEnabled(!ACcontrol && number != null && !number.isEmpty());

            // Ánh xạ các nút nâng cao nếu ở chế độ ngang
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                btBinhPhuong = findViewById(R.id.btBinhPhuong);
                btXmuY = findViewById(R.id.btXmuY);
                btCanBacHai = findViewById(R.id.btCanBacHai);
                btln = findViewById(R.id.btln);
                btlogXT = findViewById(R.id.btlogXT);
                btCanBacYCuaX = findViewById(R.id.btCanBacYCuaX);
                btArcSin = findViewById(R.id.btArcSin);
                btArcCos = findViewById(R.id.btArcCos);
                btArcTan = findViewById(R.id.btArcTan);
                btSin = findViewById(R.id.btSin);
                btCos = findViewById(R.id.btCos);
                btTan = findViewById(R.id.btTan);
                btGiaiThua = findViewById(R.id.btGiaiThua);
                btE = findViewById(R.id.btE);
                btPi = findViewById(R.id.btPi);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Lỗi ánh xạ giao diện: " + e.getMessage());
            tvResult.setText("Lỗi giao diện");
            Toast.makeText(this, "Không thể tải giao diện", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Gán sự kiện cho các nút số
        bt0.setOnClickListener(view -> numberClick("0"));
        bt1.setOnClickListener(view -> numberClick("1"));
        bt2.setOnClickListener(view -> numberClick("2"));
        bt3.setOnClickListener(view -> numberClick("3"));
        bt4.setOnClickListener(view -> numberClick("4"));
        bt5.setOnClickListener(view -> numberClick("5"));
        bt6.setOnClickListener(view -> numberClick("6"));
        bt7.setOnClickListener(view -> numberClick("7"));
        bt8.setOnClickListener(view -> numberClick("8"));
        bt9.setOnClickListener(view -> numberClick("9"));

        // Nút phép cộng
        btCong.setOnClickListener(view -> handleOperator("+", "Cong"));

        // Nút phép trừ
        btTru.setOnClickListener(view -> handleOperator("-", "Tru"));

        // Nút phép nhân
        btNhan.setOnClickListener(view -> handleOperator("*", "Nhan"));

        // Nút phép chia
        btChia.setOnClickListener(view -> handleOperator("/", "Chia"));

        // Nút bằng
        btBang.setOnClickListener(view -> {
            if (isBangLocked || (number == null && !isEqual)) return;
            if (!waitingForSecondNumber) {
                tvHistory.setText(tvHistory.getText().toString() + tvResult.getText().toString());
            }
            try {
                if (status != null && (operator || isEqual)) {
                    switch (status) {
                        case "Cong":
                            Cong();
                            break;
                        case "Tru":
                            Tru();
                            break;
                        case "Nhan":
                            Nhan();
                            break;
                        case "Chia":
                            Chia();
                            break;
                        case "XmuY":
                            XmuY();
                            break;
                        case "logXT":
                            logXT();
                            break;
                        case "YcanX":
                            YcanX();
                            break;
                    }
                    ans = firstnumber; // Lưu kết quả vào ans
                } else {
                    firstnumber = Double.parseDouble(tvResult.getText().toString());
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                }
            } catch (Exception e) {
                tvResult.setText("Lỗi");
                Log.e("MainActivity", "Lỗi phép bằng: " + e.getMessage());
                resetCalculator();
            }
            isEqual = true;
            operator = false;
            equal = true;
            waitingForSecondNumber = false;
            number = null;
            isBangLocked = true;
        });

        // Nút xóa tất cả (AC)
        btAC.setOnClickListener(view -> {
            isBangLocked = false;
            resetCalculator();
        });

        // Nút xóa ký tự cuối (C)
        btC.setOnClickListener(view -> {
            isBangLocked = false;
            if (ACcontrol) {
                tvResult.setText("0");
            } else if (number != null && number.length() > 0) {
                number = number.substring(0, number.length() - 1);
                if (number.isEmpty()) {
                    btC.setEnabled(false);
                    number = null;
                    tvResult.setText("0");
                } else {
                    if (!number.contains(".")) dot = true;
                    tvResult.setText(number);
                }
            }
        });

        // Nút dấu chấm
        btPhay.setOnClickListener(view -> {
            isBangLocked = false;
            if (dot) {
                number = (number == null) ? "0." : number + ".";
                tvResult.setText(number);
                dot = false;
            }
        });

        // Nút âm dương
        btAmDuong.setOnClickListener(view -> {
            isBangLocked = false;
            if (number != null && !number.isEmpty()) {
                try {
                    double value = Double.parseDouble(number);
                    value = -value;
                    number = String.valueOf(value);
                    tvResult.setText(number);
                } catch (NumberFormatException e) {
                    tvResult.setText("Lỗi");
                    Log.e("MainActivity", "Lỗi đổi dấu: " + e.getMessage());
                    resetCalculator();
                }
            } else {
                tvResult.setText("0");
            }
        });

        // Nút Ans
        btAns.setOnClickListener(view -> {
            isBangLocked = false;
            number = String.valueOf(ans);
            tvResult.setText(formatResult(ans));
            operator = true;
            ACcontrol = false;
            btC.setEnabled(true);
        });

        // Gán sự kiện cho các nút nâng cao nếu ở chế độ ngang
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Nút bình phương
            btBinhPhuong.setOnClickListener(view -> {
                isBangLocked = false;
                applyFunction("x²", () -> {
                    double value = Double.parseDouble(tvResult.getText().toString());
                    firstnumber = value * value;
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                });
            });

            // Nút xʸ
            btXmuY.setOnClickListener(view -> {
                isBangLocked = false;
                handleTwoOperandFunction("xʸ", "XmuY");
            });

            // Nút căn bậc hai
            btCanBacHai.setOnClickListener(view -> {
                isBangLocked = false;
                applyFunction("²√x", () -> {
                    double value = Double.parseDouble(tvResult.getText().toString());
                    if (value < 0) {
                        tvResult.setText("Lỗi: Số âm");
                        return;
                    }
                    firstnumber = Math.sqrt(value);
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                });
            });

            // Nút ln
            btln.setOnClickListener(view -> {
                isBangLocked = false;
                applyFunction("ln", () -> {
                    double value = Double.parseDouble(tvResult.getText().toString());
                    if (value <= 0) {
                        tvResult.setText("Lỗi: ≤ 0");
                        return;
                    }
                    firstnumber = Math.log(value);
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                });
            });

            // Nút logₓy
            btlogXT.setOnClickListener(view -> {
                isBangLocked = false;
                handleTwoOperandFunction("logₓy", "logXT");
            });

            // Nút căn bậc y
            btCanBacYCuaX.setOnClickListener(view -> {
                isBangLocked = false;
                handleTwoOperandFunction("ʸ√x", "YcanX");
            });

            // Nút arcsin
            btArcSin.setOnClickListener(view -> {
                isBangLocked = false;
                applyFunction("sin⁻¹", () -> {
                    double value = Double.parseDouble(tvResult.getText().toString());
                    if (value < -1 || value > 1) {
                        tvResult.setText("Lỗi: [-1,1]");
                        return;
                    }
                    firstnumber = Math.asin(value);
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                });
            });

            // Nút arccos
            btArcCos.setOnClickListener(view -> {
                isBangLocked = false;
                applyFunction("cos⁻¹", () -> {
                    double value = Double.parseDouble(tvResult.getText().toString());
                    if (value < -1 || value > 1) {
                        tvResult.setText("Lỗi: [-1,1]");
                        return;
                    }
                    firstnumber = Math.acos(value);
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                });
            });

            // Nút arctan
            btArcTan.setOnClickListener(view -> {
                isBangLocked = false;
                applyFunction("tan⁻¹", () -> {
                    double value = Double.parseDouble(tvResult.getText().toString());
                    firstnumber = Math.atan(value);
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                });
            });

            // Nút sin
            btSin.setOnClickListener(view -> {
                isBangLocked = false;
                applyFunction("sin", () -> {
                    double value = Double.parseDouble(tvResult.getText().toString());
                    firstnumber = Math.sin(value);
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                });
            });

            // Nút cos
            btCos.setOnClickListener(view -> {
                isBangLocked = false;
                applyFunction("cos", () -> {
                    double value = Double.parseDouble(tvResult.getText().toString());
                    firstnumber = Math.cos(value);
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                });
            });

            // Nút tan
            btTan.setOnClickListener(view -> {
                isBangLocked = false;
                applyFunction("tan", () -> {
                    double value = Double.parseDouble(tvResult.getText().toString());
                    if (Math.abs(value % Math.PI) == Math.PI / 2) {
                        tvResult.setText("Lỗi: Undefined");
                        return;
                    }
                    firstnumber = Math.tan(value);
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                });
            });

            // Nút giai thừa
            btGiaiThua.setOnClickListener(view -> {
                isBangLocked = false;
                applyFunction("x!", () -> {
                    double value = Double.parseDouble(tvResult.getText().toString());
                    if (value < 0 || value != Math.floor(value)) {
                        tvResult.setText("Lỗi: Số nguyên ≥ 0");
                        return;
                    }
                    if (value > 170) {
                        tvResult.setText("Lỗi: Giai thừa quá lớn");
                        return;
                    }
                    firstnumber = calculateFactorial((int) value);
                    tvResult.setText(formatResult(firstnumber));
                    ans = firstnumber;
                });
            });

            // Nút hằng số e
            btE.setOnClickListener(view -> {
                isBangLocked = false;
                number = String.valueOf(Math.E);
                tvResult.setText(number);
                operator = true;
                ACcontrol = false;
                btC.setEnabled(true);
            });

            // Nút hằng số pi
            btPi.setOnClickListener(view -> {
                isBangLocked = false;
                number = String.valueOf(Math.PI);
                tvResult.setText(number);
                operator = true;
                ACcontrol = false;
                btC.setEnabled(true);
            });
        }
    }

    // Lưu trạng thái khi xoay màn hình
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("NUMBER", number);
        outState.putDouble("ANS", ans);
        outState.putDouble("FIRST_NUMBER", firstnumber);
        outState.putDouble("LAST_NUMBER", lastnumber);
        outState.putString("STATUS", status);
        outState.putBoolean("OPERATOR", operator);
        outState.putBoolean("DOT", dot);
        outState.putBoolean("AC_CONTROL", ACcontrol);
        outState.putBoolean("EQUAL", equal);
        outState.putBoolean("IS_EQUAL", isEqual);
        outState.putBoolean("WAITING_FOR_SECOND_NUMBER", waitingForSecondNumber);
        outState.putString("RESULT_TEXT", tvResult.getText().toString());
        outState.putString("HISTORY_TEXT", tvHistory.getText().toString());
        outState.putBoolean("IS_BANG_LOCKED", isBangLocked);
    }

    // Xử lý nhập số
    private void numberClick(String input) {
        isBangLocked = false;
        if (equal) {
            resetCalculator();
            number = input;
        } else if (number == null || waitingForSecondNumber) {
            number = input;
            waitingForSecondNumber = false;
            tvResult.setText(number);
        } else {
            number += input;
            tvResult.setText(number);
        }
        operator = true;
        ACcontrol = false;
        btC.setEnabled(true);
        equal = false;
    }

    // Xử lý toán tử
    private void handleOperator(String symbol, String newStatus) {
        if (number == null && !isEqual) return;
        isBangLocked = false;
        try {
            if (isEqual) {
                // Sử dụng kết quả trước đó làm số đầu tiên
                firstnumber = Double.parseDouble(tvResult.getText().toString());
                tvHistory.setText(formatResult(firstnumber) + symbol);
            } else {
                if (operator && status != null) {
                    // Thực hiện phép tính trước đó
                    switch (status) {
                        case "Cong":
                            Cong();
                            break;
                        case "Tru":
                            Tru();
                            break;
                        case "Nhan":
                            Nhan();
                            break;
                        case "Chia":
                            Chia();
                            break;
                        case "XmuY":
                            XmuY();
                            break;
                        case "logXT":
                            logXT();
                            break;
                        case "YcanX":
                            YcanX();
                            break;
                    }
                    ans = firstnumber;
                    // Cập nhật tvHistory với kết quả trung gian và toán tử mới
                    tvHistory.setText(formatResult(firstnumber) + symbol);
                } else {
                    // Không có phép tính trước đó, chỉ thêm số hiện tại và toán tử
                    firstnumber = Double.parseDouble(tvResult.getText().toString());
                    tvHistory.setText(tvResult.getText().toString() + symbol);
                }
            }
            tvResult.setText("0");
        } catch (Exception e) {
            tvResult.setText("Lỗi");
            Log.e("MainActivity", "Lỗi toán tử: " + e.getMessage());
            resetCalculator();
        }
        isEqual = false;
        status = newStatus;
        operator = false;
        number = null;
        dot = true;
        waitingForSecondNumber = true;
    }

    // Xử lý hàm toán học một đối số
    private void applyFunction(String functionName, Runnable function) {
        if (number == null) return;
        isBangLocked = false;
        try {
            tvHistory.setText(functionName + "(" + tvResult.getText().toString() + ")");
            function.run();
            number = null;
            isEqual = true;
            equal = true;
        } catch (Exception e) {
            tvResult.setText("Lỗi");
            Log.e("MainActivity", "Lỗi hàm " + functionName + ": " + e.getMessage());
            resetCalculator();
        }
    }

    // Xử lý hàm yêu cầu hai đối số
    private void handleTwoOperandFunction(String functionName, String newStatus) {
        if (number == null && !isEqual) return;
        isBangLocked = false;
        try {
            firstnumber = Double.parseDouble(tvResult.getText().toString());
            if (isEqual) {
                tvHistory.setText(functionName + "(" + tvResult.getText().toString() + ",");
            } else {
                tvHistory.setText(functionName + "(" + tvResult.getText().toString() + ",");
            }
            if (operator && status != null) {
                switch (status) {
                    case "Cong":
                        Cong();
                        break;
                    case "Tru":
                        Tru();
                        break;
                    case "Nhan":
                        Nhan();
                        break;
                    case "Chia":
                        Chia();
                        break;
                    case "XmuY":
                        XmuY();
                        break;
                    case "logXT":
                        logXT();
                        break;
                    case "YcanX":
                        YcanX();
                        break;
                }
                ans = firstnumber;
                tvResult.setText(formatResult(firstnumber));
            }
            tvResult.setText("0");
        } catch (Exception e) {
            tvResult.setText("Lỗi");
            Log.e("MainActivity", "Lỗi hàm " + functionName + ": " + e.getMessage());
            resetCalculator();
        }
        isEqual = false;
        status = newStatus;
        operator = false;
        number = null;
        dot = true;
        waitingForSecondNumber = true;
    }

    // Phép cộng
    private void Cong() {
        lastnumber = Double.parseDouble(tvResult.getText().toString());
        firstnumber += lastnumber;
        tvResult.setText(formatResult(firstnumber));
        dot = true;
    }

    // Phép trừ
    private void Tru() {
        lastnumber = Double.parseDouble(tvResult.getText().toString());
        firstnumber -= lastnumber;
        tvResult.setText(formatResult(firstnumber));
        dot = true;
    }

    // Phép nhân
    private void Nhan() {
        lastnumber = Double.parseDouble(tvResult.getText().toString());
        firstnumber *= lastnumber;
        tvResult.setText(formatResult(firstnumber));
        dot = true;
    }

    // Phép chia
    private void Chia() {
        lastnumber = Double.parseDouble(tvResult.getText().toString());
        if (lastnumber == 0) {
            tvResult.setText("Lỗi: Chia 0");
            return;
        }
        firstnumber /= lastnumber;
        tvResult.setText(formatResult(firstnumber));
        dot = true;
    }

    // Lũy thừa xʸ
    private void XmuY() {
        lastnumber = Double.parseDouble(tvResult.getText().toString());
        firstnumber = Math.pow(firstnumber, lastnumber);
        tvResult.setText(formatResult(firstnumber));
        dot = true;
    }

    // Logarithm logₓy
    private void logXT() {
        lastnumber = Double.parseDouble(tvResult.getText().toString());
        if (firstnumber <= 0 || lastnumber <= 0 || firstnumber == 1) {
            tvResult.setText("Lỗi: Cơ số hoặc đối số không hợp lệ");
            resetCalculator();
            return;
        }
        firstnumber = Math.log(lastnumber) / Math.log(firstnumber);
        tvResult.setText(formatResult(firstnumber));
        dot = true;
    }

    // Căn bậc y (ʸ√x)
    private void YcanX() {
        lastnumber = Double.parseDouble(tvResult.getText().toString());
        if (lastnumber == 0) {
            tvResult.setText("Lỗi: Cơ số bằng 0");
            resetCalculator();
            return;
        }
        if (firstnumber < 0 && lastnumber % 2 == 0) {
            tvResult.setText("Lỗi: Số âm với căn chẵn");
            resetCalculator();
            return;
        }
        firstnumber = Math.pow(firstnumber, 1.0 / lastnumber);
        tvResult.setText(formatResult(firstnumber));
        dot = true;
    }

    // Tính giai thừa
    private double calculateFactorial(int n) {
        if (n == 0 || n == 1) return 1;
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    // Định dạng kết quả
    private String formatResult(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return "Lỗi";
        }
        if (Math.abs(value) > 1e6 || (Math.abs(value) < 1e-6 && value != 0)) {
            return String.format(Locale.US, "%.5e", value);
        }
        return decimalFormat.format(value);
    }

    // Đặt lại máy tính
    private void resetCalculator() {
        number = null;
        firstnumber = 0;
        lastnumber = 0;
        status = null;
        operator = false;
        dot = true;
        equal = false;
        isEqual = false;
        waitingForSecondNumber = false;
        isBangLocked = false;
        tvResult.setText("0");
        tvHistory.setText("");
        ACcontrol = true;
        btC.setEnabled(false);
    }
}
