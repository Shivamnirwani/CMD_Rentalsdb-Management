package com.company;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class ODBC {

    //Update the record
    public static void Updatebooking(Connection connection, String vid, String Pickup_Location, String Drop_Location)
    {
        try {
            Statement stmt = connection.createStatement();
            String query = "update F21_S003_17_Booking set Pickup_Location='"+Pickup_Location+"'"+", Drop_Location='"+Drop_Location+"'  WHERE Vehicle_ID='"+vid+"'";
            int rowsEffected= stmt.executeUpdate(query);
            if(rowsEffected>0)
            {
                System.out.println("Record Updated Successfully");
            }
            System.out.println();
            stmt.close();
        }
        catch (SQLException e) {

            System.out.println("error in accessing the relation");
            e.printStackTrace();
            return;

        }
    }

    public static void Removeemployee(Connection connection, String reid)
    {
        try {
            Statement stmt = connection.createStatement();
            String query = "delete from F21_S003_17_Employee where Emp_ID= '"+reid+"'";
            int rowsEffected= stmt.executeUpdate(query);
            if(rowsEffected>0)
            {
                System.out.println("Record Deleted Successfully");
            }
            System.out.println();
            stmt.close();
        }
        catch (SQLException e) {

            System.out.println("error in accessing the relation");
            e.printStackTrace();
            return;

        }
    }


    public static void ListEmp(Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from F21_S003_17_Employee");
            while (rs.next())
                System.out.println("ID:"+rs.getString("Emp_ID")+" Name:"+rs.getString("Emp_Name")+" SSN:"+rs.getString("Emp_SSN")+" Branch:"+rs.getString("Branch_ID")+" Salary:"+rs.getString("Salary"));
            rs.close();
            stmt.close();
        } catch (SQLException e) {

            System.out.println("relation not found");
            e.printStackTrace();
            return;
        }
    }
    public static void Insertemployee(Connection connection,String ssn,String ename,String eid, String email,String dob,String sal,String bid)
    {
        try {
            Statement stmt = connection.createStatement();
            String query = "INSERT INTO F21_S003_17_Employee(Emp_SSN,Emp_ID,Emp_Name,Emp_Email,Emp_DOB,Salary,Branch_ID) VALUES ('"+ssn+"','"+eid+"','"+ename+"','"+email+"','"+dob+"',"+sal+",'"+bid+"')";
            int rowsEffected= stmt.executeUpdate(query);
            if(rowsEffected>0)
            {
                System.out.println("Record Inserted Successfully");
            }
            System.out.println();
            stmt.close();
        }
        catch (SQLException e) {

            System.out.println("error in accessing the relation");
            e.printStackTrace();
            return;

        }
    }

    public static void DayRank(Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select dense_rank()over (order by count(*) desc)as Rank,day,count(*) as Occurence from (SELECT TO_CHAR(Booking_date, 'DY') Day FROM F21_S003_17_BookingCost) group by day");
            System.out.println("Rank  Day  Occurrence");
            while (rs.next())
                System.out.println(rs.getString("Rank")+"     "+rs.getString("day")+"      "+rs.getString("Occurence"));
            rs.close();
            stmt.close();
        } catch (SQLException e) {

            System.out.println("relation not found");
            e.printStackTrace();
            return;

        }
    }
    public static void Revenue(Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select rank()over (order by trip_cost desc)as Rank,Vehicle_class,Trip_cost as revenue, Maintainance_cost as operating_cost, Trip_cost-Maintainance_cost as Net_Profit from (select distinct vehicle_class,max(Trip_cost) as Trip_cost,max(mc) as Maintainance_cost from(select id as ko ,sum(Trip_cost)as trip_cost,sum(Maintainance_cost)as mc,vehicle_class from (select vehicle_id as id,Trip_cost from F21_S003_17_booking b,F21_S003_17_BookingCost bc where b.booking_id=bc.booking_id)vt,F21_S003_17_vehicle v where vt.id=v.vehicle_id group by rollup(Vehicle_class,Trip_cost,id,maintainance_cost)) group by vehicle_class order by trip_cost ) group by (Vehicle_class,trip_cost,maintainance_cost)");
            final Object[][] table = new String[9][];
            int n = 2;
            table[0] = new String[]{ "Rank", "Vehicle Class", "Operating Cost","Net Profit"};
            table[1] = new String[]{ "_____________", "______________________", "______________________","_______________________________" };
            while (rs.next()) {
                table[n] = new String[]{rs.getString("Rank"), rs.getString("Vehicle_class"), rs.getString("operating_cost"), rs.getString("Net_Profit")};
                n++;
            }
            rs.close();
            stmt.close();
            for (final Object[] row : table) {
                System.out.format("%20s%20s%20s%20s%n", row);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {

            System.out.println("relation not found");
            e.printStackTrace();
            return;

        }
    }

    public static void fpayment(Connection connection, String pay)
    {
        try
        {
            String payments = "select Owner_name,payment from F21_S003_17_Owner where (payment >?)";
            PreparedStatement preparedStatement = connection.prepareStatement(payments);
            preparedStatement.setString(1, pay);
            ResultSet rs = preparedStatement.executeQuery();
            int t=1;
            System.out.println("The owners with outstanding payments greater than "+pay+":");
            while (rs.next())
            {
                System.out.printf("%d",t);
                System.out.println(") Name:"+rs.getString("Owner_name") + "  Payment:" + rs.getString("payment"));
                t++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void frating(Connection connection, String rt)
    {
        try
        {
            String ratings = "select Vehicle_id,Vehicle_Rating from (select Booking_ID as bid,Vehicle_Rating from F21_S003_17_Reviews r where(Vehicle_Rating>=?)) rate, F21_S003_17_Booking b where rate.bid=b.Booking_ID";
            PreparedStatement preparedStatement = connection.prepareStatement(ratings);
            preparedStatement.setString(1, rt);
            ResultSet rs = preparedStatement.executeQuery();
            int t=1;
            System.out.println("The vehicles with ratings greater than and equal to "+rt+":");
            while (rs.next())
            {
                System.out.printf("%d",t);
                System.out.println(") VehicleID:"+rs.getString("Vehicle_id") + "  Rating:" + rs.getString("Vehicle_Rating"));
                t++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;
        }
        Connection connection = null;
        connection = DriverManager.getConnection("jdbc:oracle:thin:@acaddbprod-2.uta.edu:1523/pcse1p.data.uta.edu", "axs9120", "Dbms12345678");

        boolean exit = false;
                do {
                    System.out.println();
                    System.out.println("-------------------------------------------------");
                    System.out.println("Welcome to T17 Rentals Service!");
                    System.out.println("Select your choice:");
                    System.out.println("1. View available vehicles");
                    System.out.println("2. Customer Login");
                    System.out.println("3. Admin Login");
                    System.out.println("4. To Exit");
                    System.out.println("-------------------------------------------------");
                    Scanner sc1 = new Scanner(System.in);
                    int choice = sc1.nextInt();
                    switch (choice) {
                        case 1:
                            Statement stmt = connection.createStatement();
                            ResultSet rs = stmt.executeQuery("select * from F21_S003_17_Vehicle where Available='Yes'");
                            final Object[][] table = new String[23][];
                            int n = 2;
                            table[0] = new String[]{ "Vehicle ID", "Vehicle Brand", "Vehicle Class" };
                            table[1] = new String[]{ "_____________", "______________________", "______________________" };
                            while (rs.next()) {
                                table[n] = new String[]{rs.getString("Vehicle_ID"), rs.getString("Vehicle_Brand"), rs.getString("Vehicle_Class")};
                                n++;
                            }
                            rs.close();
                            stmt.close();
                            for (final Object[] row : table) {
                                System.out.format("%20s%20s%20s%n", row);
                            }
                            break;
                        case 2:
                            System.out.println("Please Login below:");
                            System.out.println("Enter username:");
                            Scanner sc = new Scanner(System.in);
                            String uname = sc.nextLine();
                            System.out.println("Enter password:");
                            Scanner sc2 = new Scanner(System.in);
                            String pass = sc2.nextLine();
                            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM F21_S003_17_Customer WHERE Username=? AND Password=?");
                            preparedStatement.setString(1, uname);
                            preparedStatement.setString(2, pass);
                            ResultSet rs2 = preparedStatement.executeQuery();

                            if (rs2.next()) {
                                System.out.println("-------------------------------------------------");
                                System.out.println("Login successful");

                                boolean cexit = false;
                                do {
                                    System.out.println("-------------------------------------------------");
                                    System.out.println("Customer Access Area");
                                    System.out.println("Welcome" + " " + rs2.getString("Cust_Name"));
                                    System.out.println("Select your choice:");
                                    System.out.println("1. View account information");
                                    System.out.println("2. View Bookings");
                                    System.out.println("3. Edit Bookings");
                                    System.out.println("4. Exit back to Home Page");
                                    System.out.println("-------------------------------------------------");
                                    String vid= rs2.getString("Vehicle_ID");
                                    Scanner sc3 = new Scanner(System.in);
                                    int choice2 = sc3.nextInt();
                                    switch (choice2) {
                                        case 1:
                                            System.out.println("Username:" + rs2.getString("Username") + " Name:" + rs2.getString("Cust_Name") + " Email:" + rs2.getString("Cust_Email"));
                                            break;
                                        case 2:
                                            PreparedStatement preparedStatement2 = connection.prepareStatement("select * from F21_S003_17_Booking WHERE Vehicle_ID=?");
                                            preparedStatement2.setString(1, vid);
                                            ResultSet rs22 = preparedStatement2.executeQuery();

                                            final Object[][] table22 = new String[3][];
                                            int mm = 2;
                                            table22[0] = new String[]{ "Booking ID", "Pickup Location", "Drop Location" };
                                            table22[1] = new String[]{ "_____________", "______________________", "______________________" };
                                            while (rs22.next()) {
                                                table22[mm] = new String[]{rs22.getString("Booking_ID"), rs22.getString("Pickup_Location"), rs22.getString("Drop_Location")};
                                                mm++;
                                            }
                                            rs22.close();
                                            preparedStatement2.close();
                                            for (final Object[] row : table22) {
                                                System.out.format("%20s%20s%20s%n", row);
                                            }
                                            break;
                                        case 3:
                                            System.out.println("Editing your booking:");
                                            System.out.println("Enter new pickup location:");
                                            Scanner pu = new Scanner(System.in);
                                            String pick = pu.nextLine();
                                            System.out.println("Enter new drop location:");
                                            Scanner dr = new Scanner(System.in);
                                            String drop = dr.nextLine();
                                            Updatebooking(connection, vid, pick, drop);
                                            break;
                                        case 4:
                                            cexit = true;
                                            System.out.println("Redirecting to Home Page");
                                            break;
                                        default:
                                            System.out.println("Invalid input choice");
                                            break;
                                    }
                                } while (!cexit);
                                } else {
                                System.out.println("User not found or incorrect password!");
                            }
                            break;
                        case 3:
                            System.out.println("Please Login below:");
                            System.out.println("Enter username:");
                            Scanner sc4 = new Scanner(System.in);
                            String aname = sc4.nextLine();
                            System.out.println("Enter password:");
                            Scanner sc5 = new Scanner(System.in);
                            String apass = sc5.nextLine();

                            if (Objects.equals(aname, "admin") && Objects.equals(apass, "admin")) {
                                System.out.println("-------------------------------------------------");
                                System.out.println("Login successful");

                                boolean aexit = false;
                                do {
                                    System.out.println("-------------------------------------------------");
                                    System.out.println("Admin Access Area");
                                    System.out.println("Select your choice:");
                                    System.out.println("1. View Employees information");
                                    System.out.println("2. View all Bookings");
                                    System.out.println("3. Add new employee");
                                    System.out.println("4. Remove employee");
                                    System.out.println("5. Generate Reports");
                                    System.out.println("6. Exit back to Home Page");
                                    System.out.println("-------------------------------------------------");
                                    Scanner sc6 = new Scanner(System.in);
                                    int choice3 = sc6.nextInt();
                                    switch (choice3) {
                                        case 1:
                                            ListEmp(connection);
                                            break;
                                        case 2:
                                            Statement stmt2 = connection.createStatement();
                                            ResultSet rs3 = stmt2.executeQuery("select * from F21_S003_17_Booking");
                                            final Object[][] table2 = new String[41][];
                                            int m = 2;
                                            table2[0] = new String[]{ "Booking ID", "Pickup Location", "Drop Location", "Customer", "Vehicle ID" };
                                            table2[1] = new String[]{ "_____________", "______________________", "______________________", "______________________", "______________________" };
                                            while (rs3.next()) {
                                                table2[m] = new String[]{rs3.getString("Booking_ID"), rs3.getString("Pickup_Location"), rs3.getString("Drop_Location"), rs3.getString("Cust_Name"), rs3.getString("Vehicle_ID")};
                                                m++;
                                            }
                                            rs3.close();
                                            stmt2.close();
                                            for (final Object[] row : table2) {
                                                System.out.format("%20s%20s%20s%20s%20s%n", row);
                                            }
                                            break;
                                        case 3:
                                            System.out.println("Adding new Employee...");
                                            System.out.println("Enter Employee Name:");
                                            Scanner en = new Scanner(System.in);
                                            String ename = en.nextLine();
                                            System.out.println("Enter Employee SSN:");
                                            Scanner es = new Scanner(System.in);
                                            String ssn = es.nextLine();
                                            System.out.println("Enter Employee ID:");
                                            Scanner ei = new Scanner(System.in);
                                            String eid = ei.nextLine();
                                            System.out.println("Enter Employee Email:");
                                            Scanner em = new Scanner(System.in);
                                            String email = em.nextLine();
                                            System.out.println("Enter Employee DOB:");
                                            Scanner d = new Scanner(System.in);
                                            String dob = d.nextLine();
                                            System.out.println("Enter Employee Salary:");
                                            Scanner sa = new Scanner(System.in);
                                            String sal = sa.nextLine();
                                            System.out.println("Enter Employee BranchID:");
                                            Scanner bi = new Scanner(System.in);
                                            String bid = bi.nextLine();
                                            Insertemployee(connection, ssn, ename,eid ,email, dob, sal, bid);
                                            break;
                                        case 4:
                                            System.out.println("Removing an employee");
                                            System.out.println("Enter Employee ID:");
                                            Scanner rid = new Scanner(System.in);
                                            String reid = rid.nextLine();
                                            Removeemployee(connection, reid);
                                            break;
                                        case 5:
                                            boolean rexit = false;
                                            do {
                                                System.out.println("-------------------------------------------------");
                                                System.out.println("Report Generation");
                                                System.out.println("Select the report you want to generate:");
                                                System.out.println("1. View outstanding payments to owners (more than the specified value)");
                                                System.out.println("2. View vehicles which have ratings over specific value");
                                                System.out.println("3. Display days with most booking");
                                                System.out.println("4. What is the revenue generated and operating cost by each class of vehicle?");
                                                System.out.println("5. Exit back to Admin Page");
                                                System.out.println("-------------------------------------------------");
                                                Scanner sup = new Scanner(System.in);
                                                int choice4 = sup.nextInt();
                                                switch (choice4) {
                                                    case 1:
                                                        System.out.println("Enter the minimum outstanding payment amount:");
                                                        Scanner amt = new Scanner(System.in);
                                                        String pay = amt.nextLine();
                                                        fpayment(connection, pay);
                                                        break;
                                                    case 2:
                                                        System.out.println("Enter the minimum allowed rating:");
                                                        Scanner rat = new Scanner(System.in);
                                                        String rt = rat.nextLine();
                                                        frating(connection, rt);
                                                        break;
                                                    case 3:
                                                        DayRank(connection);
                                                        break;
                                                    case 4:
                                                        Revenue(connection);
                                                        break;
                                                    case 5:
                                                        rexit = true;
                                                        System.out.println("Redirecting to Admin Page");
                                                        break;
                                                    default:
                                                        System.out.println("Invalid input choice");
                                                        break;
                                                }
                                            } while (!rexit);
                                            break;
                                        case 6:
                                            aexit = true;
                                            System.out.println("Redirecting to Home Page");
                                            break;
                                        default:
                                            System.out.println("Invalid input choice");
                                            break;
                                    }
                                } while (!aexit);
                            } else {
                                System.out.println("Login denied!");
                            }
                            break;

                        case 4:
                            exit = true;
                            System.out.println("Thank you for visiting");
                            break;
                        default:
                            System.out.println("Invalid input choice");
                            break;
                    }
                } while (!exit);
        connection.close();
    }
}