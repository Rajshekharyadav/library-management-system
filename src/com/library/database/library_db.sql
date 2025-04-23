-- Library Management System Database Setup

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS `library_management_system`;
USE `library_management_system`;

-- Books table
DROP TABLE IF EXISTS `books`;
CREATE TABLE IF NOT EXISTS `books` (
  `book_id` INT(11) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `author` VARCHAR(255) NOT NULL,
  `publisher` VARCHAR(255) NOT NULL,
  `isbn` VARCHAR(20) NOT NULL,
  `category` VARCHAR(100) NOT NULL,
  `quantity` INT(11) NOT NULL,
  `available` INT(11) NOT NULL,
  `added_date` DATE NOT NULL,
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Members table
DROP TABLE IF EXISTS `members`;
CREATE TABLE IF NOT EXISTS `members` (
  `member_id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `address` TEXT NOT NULL,
  `member_type` ENUM('Student', 'Faculty', 'Staff') NOT NULL,
  `join_date` DATE NOT NULL,
  `status` ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
  PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Book Issues table
DROP TABLE IF EXISTS `book_issues`;
CREATE TABLE IF NOT EXISTS `book_issues` (
  `issue_id` INT(11) NOT NULL AUTO_INCREMENT,
  `book_id` INT(11) NOT NULL,
  `member_id` INT(11) NOT NULL,
  `issue_date` DATE NOT NULL,
  `due_date` DATE NOT NULL,
  `return_date` DATE DEFAULT NULL,
  `fine_amount` DECIMAL(10,2) DEFAULT 0.00,
  `status` ENUM('Issued', 'Returned', 'Overdue') NOT NULL DEFAULT 'Issued',
  PRIMARY KEY (`issue_id`),
  FOREIGN KEY (`book_id`) REFERENCES `books`(`book_id`) ON DELETE CASCADE,
  FOREIGN KEY (`member_id`) REFERENCES `members`(`member_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Users table (for staff login)
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `full_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `role` ENUM('Administrator', 'Librarian') NOT NULL,
  `last_login` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert default admin user
INSERT INTO `users` (`username`, `password`, `full_name`, `email`, `role`) 
VALUES ('admin', 'admin123', 'System Administrator', 'admin@library.com', 'Administrator');

-- Insert sample books
INSERT INTO `books` (`title`, `author`, `publisher`, `isbn`, `category`, `quantity`, `available`, `added_date`) VALUES
('Introduction to Java Programming', 'Y. Daniel Liang', 'Pearson', '978-0134670942', 'Programming', 5, 5, CURDATE()),
('Database System Concepts', 'Abraham Silberschatz', 'McGraw Hill', '978-0073523323', 'Database', 3, 3, CURDATE()),
('Clean Code', 'Robert C. Martin', 'Prentice Hall', '978-0132350884', 'Software Engineering', 2, 2, CURDATE()),
('The Pragmatic Programmer', 'Andrew Hunt', 'Addison-Wesley', '978-0201616224', 'Software Engineering', 3, 3, CURDATE()),
('Design Patterns', 'Erich Gamma', 'Addison-Wesley', '978-0201633610', 'Software Engineering', 2, 2, CURDATE());

-- Insert sample members
INSERT INTO `members` (`name`, `email`, `phone`, `address`, `member_type`, `join_date`, `status`) VALUES
('John Smith', 'john.smith@example.com', '555-123-4567', '123 Main St, Anytown, USA', 'Student', CURDATE(), 'Active'),
('Jane Doe', 'jane.doe@example.com', '555-234-5678', '456 Oak Ave, Anytown, USA', 'Faculty', CURDATE(), 'Active'),
('Mike Johnson', 'mike.johnson@example.com', '555-345-6789', '789 Pine Blvd, Anytown, USA', 'Student', CURDATE(), 'Active'); 