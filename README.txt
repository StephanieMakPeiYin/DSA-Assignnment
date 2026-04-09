================================================================================
         TARUMT FACILITIES BOOKING SYSTEM - README
================================================================================

OVERVIEW
--------
A console-based Java application for booking facilities (rooms) at TARUMT.
Students can book and cancel rooms. Staff can manage users, view booking
details, and manage facilities.


--------------------------------------------------------------------------------
REQUIREMENTS
--------------------------------------------------------------------------------
- Java Development Kit (JDK) 17 or later
- A terminal / command prompt


--------------------------------------------------------------------------------
PROJECT STRUCTURE
--------------------------------------------------------------------------------
DSA-Assignment/
  src/
    adt/            - Custom ADT interfaces and implementations (ListInterface, ArrayListADT)
    boundary/       - UI classes (AuthenticationUI, BookingUI, StudentUI, StaffUI)
    com/            - Entry point (Main.java)
    control/        - Business logic (AuthenticationControl, BookingControl, ...)
    entity/         - Data models (Booking, Room, User)
    util/           - Utility / helper classes
    bookings.txt    - Persistent booking data
    facility.txt    - Persistent facility data
    users.txt       - Persistent user data
  README.txt        - This file


--------------------------------------------------------------------------------
STEP 1 - COMPILE
--------------------------------------------------------------------------------
Open a terminal and navigate to the root project folder:

    cd "c:\DSA\DSA-Assignnment"

Compile all source files into the bin/ folder:

    javac -d bin -sourcepath src src/com/Main.java

If successful, no error output will appear.


--------------------------------------------------------------------------------
STEP 2 - RUN
--------------------------------------------------------------------------------
From the same root project folder, run the application:

    java -cp bin com.Main

IMPORTANT: The application must be run from the project root folder so that
the data files (src/bookings.txt, src/facility.txt, src/users.txt) are found
correctly using relative paths.


--------------------------------------------------------------------------------
STEP 3 - LOGIN
--------------------------------------------------------------------------------
On startup, you will see the Authentication Menu:

    ========================================
    = TARUMT FACILITIES BOOKING SYSTEM     =
    ========================================
    1. Login
    2. Register (new student accounts only)
    0. Exit

---- Logging in as STAFF ----
    Email    : staff@gmail.com
    Password : staff123

    Staff can:
      - Manage users (add / update / remove / recover / search)
      - View and filter booking details (by date or status)
      - Manage facilities (add / update / delete / recover / view)

---- Logging in as STUDENT ----
    Example credentials:
    Email    : step@gmail.com
    Password : step123

    Students can:
      - View available rooms (all rooms or sorted by block & capacity)
      - Book a room (select date → capacity → room → time slot → confirm)
      - Cancel an active booking (only their own bookings)
      - View their booking history (all / active / cancelled)

---- Registering a new Student account ----
    Select option 2 at the Authentication Menu.
    Provide: Full Name, Email, Password, Confirm Password.
    After successful registration you will be redirected to login.


--------------------------------------------------------------------------------
BOOKING WORKFLOW (STUDENT)
--------------------------------------------------------------------------------
1. Login as a student.
2. Select  1. Room Booking  from the student menu.
3. Inside the Booking Module:

   [Book a Room]
   - Select  2. Book Room
   - Enter a valid booking date (yyyy-MM-dd, must be within the next 3 days,
     e.g. if today is 2026-04-03 then valid dates are 2026-04-04 to 2026-04-06)
   - Enter required capacity (e.g. 10)
   - A list of rooms matching that capacity will be shown
   - Enter the Room ID you want (e.g. A101)
   - Available time slots for that room & date are shown:
       1. 09:00-11:00
       2. 11:00-13:00
       3. 13:00-15:00
       4. 15:00-17:00
       5. 17:00-19:00
   - Enter a slot number (1-5) or type the full slot (e.g. 09:00-11:00)
   - Confirm the booking (y/n)

   [Cancel a Booking]
   - Select  3. Cancel Booking
   - Your active bookings are displayed
   - Enter the booking ID (e.g. B1)
   - Select a cancellation reason
   - Confirm cancellation (y/n)

   [View My Bookings]
   - Select  4. View Bookings
   - Choose to view: All / Active only / Cancelled only


--------------------------------------------------------------------------------
BOOKING POLICY
--------------------------------------------------------------------------------
- Rooms can only be booked for the NEXT 3 DAYS (not same day).
- Available time slots: 09:00-11:00, 11:00-13:00, 13:00-15:00,
                        15:00-17:00, 17:00-19:00.
- A room slot that is already booked (ACTIVE) cannot be double-booked.
- Bookings for past dates cannot be cancelled.
- Students can only cancel their own bookings.
- Room booked will be forfeited after 10 minutes if student does not show up.


--------------------------------------------------------------------------------
DATA FILES
--------------------------------------------------------------------------------
All data is persisted in plain-text pipe-delimited files inside src/:

  bookings.txt  — Format: bookingID|roomID|date|timeSlot|status|cancelReason|studentName|studentEmail
  facility.txt  — Room/facility records
  users.txt     — Registered user accounts

These files are read on startup and written on every change. Do not edit them
manually while the application is running.


--------------------------------------------------------------------------------
TROUBLESHOOTING
--------------------------------------------------------------------------------
Problem : "Error loading bookings from file" on startup
Solution: Ensure you are running the application from the project root folder,
          not from inside src/ or bin/.

Problem : Booking shows "Unknown" for student name
Solution: Always log in before booking; do not use the default no-arg
          constructor path. The shared BookingControl is initialised in Main.

Problem : Room ID not recognised
Solution: Room IDs are case-insensitive; typing a101 is treated as A101.

Problem : Compilation error - "package adt does not exist"
Solution: Ensure you compile from the project root with -sourcepath src as
          shown in Step 1.

================================================================================
