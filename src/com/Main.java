package com;

import boundary.AuthenticationUI;
import boundary.StaffUI;
import boundary.StudentUI;
import control.AuthenticationControl;
import control.BookingControl;
import control.FacilityControl;

public class Main {

    public static void main(String[] args) {

        // Initialize controls
        AuthenticationControl authControl = new AuthenticationControl();
        control.UserControl userControl = new control.UserControl();
        FacilityControl facilityControl = new FacilityControl();
        BookingControl bookingControl = new BookingControl(facilityControl);

        // Start with authentication
        AuthenticationUI authUI = new AuthenticationUI(authControl);

        while (true) {
            // Show authentication screen
            if (!authUI.startAuthentication()) {
                System.out.println("\n╔═══════════════════════════════════════════════════════╗");
                System.out.println("║  Thank you for using TARUMT FACILITIES BOOKING system ║");
                System.out.println("╚═══════════════════════════════════════════════════════╝");
                break;
            }

            // Route to appropriate interface based on user type
            if (authControl.isStaff()) {
                StaffUI staffUI = new StaffUI(userControl, bookingControl, facilityControl);
                staffUI.start();
                facilityControl.saveFacilitiesToFile();
            } else if (authControl.isStudent()) {
                StudentUI studentUI = new StudentUI(facilityControl);
                studentUI.start();
            }

            // Logout after exiting the respective UI
            authControl.logout();
        }
    }
}
