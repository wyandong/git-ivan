package test.com.ivan;


import main.com.ivan.Calc;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CalcTest {

    // ==================== add ====================

    @Test
    @DisplayName("add: 正数相加")
    void add_positiveNumbers() {
        assertEquals(5, Calc.add(2, 3));
    }

    @Test
    @DisplayName("add: 负数相加")
    void add_negativeNumbers() {
        assertEquals(-5, Calc.add(-2, -3));
    }

    @Test
    @DisplayName("add: 零参与")
    void add_withZero() {
        assertEquals(7, Calc.add(7, 0));
    }

    // ==================== subtract ====================

    @Test
    @DisplayName("subtract: 正常相减")
    void subtract_normal() {
        assertEquals(2, Calc.subtract(5, 3));
    }

    @Test
    @DisplayName("subtract: 结果为负数")
    void subtract_resultNegative() {
        assertEquals(-3, Calc.subtract(2, 5));
    }

    // ==================== multiply ====================

    @Test
    @DisplayName("multiply: 正数相乘")
    void multiply_positiveNumbers() {
        assertEquals(6, Calc.multiply(2, 3));
    }

    @Test
    @DisplayName("multiply: 乘以零")
    void multiply_byZero() {
        assertEquals(0, Calc.multiply(100, 0));
    }

    @Test
    @DisplayName("multiply: 负数相乘")
    void multiply_negativeNumbers() {
        assertEquals(6, Calc.multiply(-2, -3));
    }

    // ==================== factorial ====================

    @Test
    @DisplayName("factorial: 0! = 1")
    void factorial_zero() {
        assertEquals(1, Calc.factorial(0));
    }

    @Test
    @DisplayName("factorial: 1! = 1")
    void factorial_one() {
        assertEquals(1, Calc.factorial(1));
    }

    @Test
    @DisplayName("factorial: 5! = 120")
    void factorial_five() {
        assertEquals(120, Calc.factorial(5));
    }

    @Test
    @DisplayName("factorial: 负数抛出 IllegalArgumentException")
    void factorial_negative() {
        assertThrows(IllegalArgumentException.class, () -> Calc.factorial(-1));
    }

    // ==================== divide ====================

    @Test
    @DisplayName("divide: 正常除法")
    void divide_normal() {
        assertEquals(2.5, Calc.divide(5.0, 2.0), 0.001);
    }

    @Test
    @DisplayName("divide: 整除")
    void divide_exactDivision() {
        assertEquals(3.0, Calc.divide(9.0, 3.0), 0.001);
    }

    @Test
    @DisplayName("divide: 被除数为零")
    void divide_dividendZero() {
        assertEquals(0.0, Calc.divide(0.0, 5.0), 0.001);
    }

    @Test
    @DisplayName("divide: 除数为零抛出 IllegalArgumentException")
    void divide_divisorZero() {
        assertThrows(IllegalArgumentException.class, () -> Calc.divide(5.0, 0.0));
    }
}