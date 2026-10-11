# Employee Payroll Management System (Java + JDBC + MySQL)

## Setup
1. Install JDK 11+ and MySQL 8.
2. Run the schema (creates `payroll_db`, tables, 3 sample employees):
   `mysql -u root -p < sql/schema.sql`
3. Set DB credentials (defaults: user `root`, password `root`):
   - Linux/Mac: `export DB_USER=root DB_PASS=yourpassword`
   - Windows:   `set DB_USER=root` and `set DB_PASS=yourpassword`

## Run with Maven
`mvn compile exec:java`

## Run without Maven
Download mysql-connector-j-8.4.0.jar into `lib/`, then:
```
mkdir out
javac -d out $(find src -name "*.java")          # Windows: javac -d out src\main\java\com\payroll\*\*.java
java -cp "out:lib/mysql-connector-j-8.4.0.jar" com.payroll.app.Main   # Windows: use ; instead of :
```
Eclipse/IntelliJ/NetBeans: open as Maven project (or add the connector jar to the build path) and run `Main`.

## Salary rules (edit in PayrollCalculator.java)
Gross = Basic + HRA 20% + DA 10% + TA 5% + Overtime (2x hourly rate)
Net = Gross - PF 12% of basic - Professional Tax (Rs 200) - Income tax (TDS) - Loss of Pay (basic/working days x absent days)

## Quick start
Put the connector jar in `lib/`, then run `run.bat` (Windows) or `./run.sh` (Linux/Mac).
