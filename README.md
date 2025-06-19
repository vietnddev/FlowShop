# 📌 FlowShop – Sales Management System

### Guideline
- Introduction
- Key features
- Database
- Tech stack
- Setup

----

## 🧾 Introduction

FlowShop is a sales management system designed specifically for my fashion store, Flowiee.  
It helps automate business processes, reduce manual work, and enhance store management efficiency.

- **Purpose**: Assist store owners in managing products, orders, inventory, finances, and employees  
- **Development Team**: Solo Developer  

---

## 🚀 Key Features

### 🛒 Product & Promotion Management
- Support product variations & custom attributes  
- Manage combo products  
- Apply discount vouchers & time-based promotions  

### 📦 Inventory & Supplier Management
- Track stock entries, exits, and inventory levels  
- Manage raw materials and supplier payments history  

### 📜 Order & Payment Processing
- Create, update, delete (CRUD) orders  
- Update payment status and release stock  
- Auto-generate QR codes for quick order lookup  

### 📊 Reports & Financial Management
- Sales reports by day, month, and sales channel  
- Track income, expenses, and financial balance  
- Employee performance reports  

### 🔐 System Management
- **User Role Management**: Control access permissions for different user roles  
- **System Configuration**: Admin can customize settings for system flexibility  
- **Scheduled Tasks**: Automate daily checks for expired products, clean up old logs, etc  
- **Audit & Logging**:
  - Track all create, update (audit on each field change), and delete actions  
  - Log event details (AOP) for debugging (processing time, request parameters, request body)  
  - Notify the administrator via email when an exception occurs (configurable)  
- **Multi-language Support (Upcoming Feature)**: Expanding message repository for localization  

---

## 💾 Database Management

The system currently manages **over 70 entities**, ensuring comprehensive data organization and efficient operations.

---

## 🛠️ Technologies Used

- **Backend**: Java 21, Spring Boot 3.2.2, Maven 3.9.5
- **Frontend**: JavaScript, Ajax, Bootstrap
- **Database**: MySQL

---

## Setup
`mvn spring-boot:run`

`mvn clean package`

---

## 🖼️ Some Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/6d1c0163-e26f-49f5-8d6e-69438b5e3ac0" alt="Login Page" width="600"/>
  <br>
  <i>Login Page</strong>
</p>


<p align="center">
  <img src="https://github.com/user-attachments/assets/8054af8b-dda6-4838-92a9-711076eebedb" alt="Login Page" width="600"/>
  <br>
  <i>Product List</strong>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/ef1189c8-ef95-4b5f-926b-a4734bcc540e" alt="Login Page" width="600"/>
  <br>
  <i>Product Variation Details</strong>
</p>
    
<p align="center">
  <img src="https://github.com/user-attachments/assets/934cc6b9-f654-4d80-8b19-43c975d2d80d" alt="Login Page" width="600"/>
  <br>
  <i>Product Selection Pop-up</strong>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/ee3a1305-6626-4e78-80c6-2095a1c4020b" alt="Login Page" width="600"/>
  <br>
  <i>Order Tracking via QR Code</strong>
</p>
    
<p align="center">
  <img src="https://github.com/user-attachments/assets/489be18d-15f8-41f5-ab12-bbb4f61857d3" alt="Login Page" width="600"/>
  <br>
  <i>System Configuration</strong>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/615c4a52-7e94-46dd-b5d3-6148e93f78a2" alt="Login Page" width="600"/>
  <br>
  <i>System Categories</strong>
</p>
