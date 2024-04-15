Application Overview:

The "Person Management System" is a  data management system that allows storing information about various types of individuals, such as employees, students, and retirees. Each person is identified by their first name, last name, PESEL (unique), height, weight, and email address. Additionally, depending on the type of person, additional information is stored, such as university name, year of study, field of study, salary, pension, etc.

Key Features:
Person Search: There is a single universal endpoint for searching individuals based on various criteria, such as type, first name, last name, age, PESEL, gender, height, weight, email address, employee salary, student university name, etc. It also supports pagination.

Person Addition: There is a single endpoint for adding new individuals to the system without modifying existing classes. The contract requires meaningful validation and error handling.

Person Editing: There is an endpoint for editing personal data. It handles the issue of concurrent data access and prevents data from being overwritten by other transactions.

Employee Position Management: Endpoint for assigning positions to employees for a specified period, ensuring that dates do not overlap.

Importing Persons from CSV File: Ability to load large CSV files into the database in a non-blocking manner, with progress monitoring and import status.

Security:
Endpoints are secured by an authentication system.
Only administrators can add, edit, and import individuals.
Assigning positions to employees can be done by an administrator or another employee.
The application is based on the Spring Boot 3.0.6 framework.
