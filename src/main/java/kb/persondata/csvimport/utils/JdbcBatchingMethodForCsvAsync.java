package kb.persondata.csvimport.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JdbcBatchingMethodForCsvAsync {
    private final JdbcTemplate jdbcTemplate;

    public void saveBatch(List<String[]> batch) {
        String sql = "INSERT INTO person (email_address, entity_type, first_name, height, last_name, personal_number,"
                + " version, weight, actual_position, actual_salary, actual_work_from, pension_value," +
                " years_worked, academic_year, course_name, scholarship, university_name, dtype) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                String[] personData = batch.get(i);
                preparedStatement.setString(1, personData[6]);
                preparedStatement.setLong(7, 0L);
                preparedStatement.setString(18, capitalizeFirstLetter(personData[0]));
                preparedStatement.setString(2, personData[0]);
                preparedStatement.setString(3, personData[1]);
                preparedStatement.setDouble(4, parseDoubleOrNull(personData[4]));
                preparedStatement.setString(5, personData[2]);
                preparedStatement.setString(6, personData[3]);
                preparedStatement.setDouble(8, parseDoubleOrNull(personData[5]));
                preparedStatement.setString(9, personData[9]);
                preparedStatement.setBigDecimal(10, parseBigDecimalOrNull(personData[10]));
                preparedStatement.setDate(14, parseDateOrNull(personData[8]));
                preparedStatement.setDate(11, parseDateOrNull(personData[11]));
                preparedStatement.setBigDecimal(12, parseBigDecimalOrNull(personData[12]));
                preparedStatement.setObject(13, parseIntOrNull(personData[13]));
                preparedStatement.setString(15, personData[9]);
                preparedStatement.setBigDecimal(16, parseBigDecimalOrNull(personData[10]));
                preparedStatement.setString(17, personData[7]);
            }

            @Override
            public int getBatchSize() {
                return batch.size();
            }
        });
        log.info("Batch saved " + batch.size() + " records");
    }

    private String capitalizeFirstLetter(String input) {
        return Character.toUpperCase(input.charAt(0)) +
                input.substring(1).toLowerCase();
    }

    private Double parseDoubleOrNull(String value) {
        if (value != null && !value.isEmpty()) {
            return Double.parseDouble(value);
        }
        return null;
    }

    private BigDecimal parseBigDecimalOrNull(String value) {
        if (value != null && !value.isEmpty()) {
            return new BigDecimal(value);
        }
        return null;
    }

    private Integer parseIntOrNull(String value) {
        if (value != null && !value.isEmpty()) {
            return Integer.parseInt(value);
        }
        return null;
    }

    private Date parseDateOrNull(String value) {
        if (value != null && !value.isEmpty()) {
            return java.sql.Date.valueOf(LocalDate.parse(value));
        }
        return null;
    }
}
