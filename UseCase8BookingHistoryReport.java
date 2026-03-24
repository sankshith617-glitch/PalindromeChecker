import java.util.*;

public class UseCase8BookingHistoryReport {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay App ");
        System.out.println(" Version: 1.0.8 (UC8)");
        System.out.println(" Module: Booking History & Reporting");
        System.out.println("====================================");

        // Initialize Inventory Service
        InventoryServiceUC8 inventory = new InventoryServiceUC8();
        inventory.addRoomType("Single", 5);
        inventory.addRoomType("Double", 3);
        inventory.addRoomType("Suite", 2);

        // Initialize Booking History
        BookingHistory bookingHistory = new BookingHistory();

        // Initialize Add-On Service Manager
        AddOnServiceManagerUC8 serviceManager = new AddOnServiceManagerUC8();

        // Initialize Booking Service
        BookingServiceUC8 bookingService = new BookingServiceUC8(inventory, serviceManager, bookingHistory);

        // Add some booking requests
        bookingService.addBookingRequest(new BookingRequestUC8("Alice", "Single"));
        bookingService.addBookingRequest(new BookingRequestUC8("Bob", "Double"));
        bookingService.addBookingRequest(new BookingRequestUC8("Charlie", "Single"));
        bookingService.addBookingRequest(new BookingRequestUC8("Diana", "Suite"));
        bookingService.addBookingRequest(new BookingRequestUC8("Eve", "Double"));

        // Process booking requests
        bookingService.processBookings();

        // Display final inventory and allocations
        inventory.displayInventory();
        inventory.displayAllocations();

        // Add services to reservations
        System.out.println("\n====================================");
        System.out.println(" Adding Optional Services");
        System.out.println("====================================");

        serviceManager.addServiceToReservation("Single-1", new AddOnServiceUC8("WiFi Premium", 15.0));
        serviceManager.addServiceToReservation("Single-1", new AddOnServiceUC8("Breakfast Buffet", 25.0));
        serviceManager.addServiceToReservation("Double-2", new AddOnServiceUC8("Airport Transfer", 30.0));
        serviceManager.addServiceToReservation("Suite-3", new AddOnServiceUC8("WiFi Premium", 15.0));
        serviceManager.addServiceToReservation("Suite-3", new AddOnServiceUC8("Spa Package", 100.0));

        // Display services and costs for each reservation
        System.out.println("\n====================================");
        System.out.println(" Reservation Services & Additional Costs");
        System.out.println("====================================");
        serviceManager.displayReservationServices();

        // Display booking history and generate reports
        System.out.println("\n====================================");
        System.out.println(" Booking History & Operational Reports");
        System.out.println("====================================");
        BookingReportService reportService = new BookingReportService();

        reportService.displayBookingHistory(bookingHistory);
        reportService.displaySummaryReport(bookingHistory, serviceManager);
        reportService.displayRoomTypeReport(bookingHistory);
        reportService.displayGuestReport(bookingHistory);
    }
}

class BookingRequestUC8 {
    String guestName;
    String roomType;

    public BookingRequestUC8(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "BookingRequest{guestName='" + guestName + "', roomType='" + roomType + "'}";
    }
}

class Reservation {
    String reservationId;
    String guestName;
    String roomType;
    String roomId;
    long confirmationTime;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.confirmationTime = System.currentTimeMillis();
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public long getConfirmationTime() {
        return confirmationTime;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", guestName='" + guestName + '\'' +
                ", roomType='" + roomType + '\'' +
                ", roomId='" + roomId + '\'' +
                ", confirmationTime=" + confirmationTime +
                '}';
    }
}

class BookingHistory {
    private List<Reservation> reservations = new ArrayList<>();
    private int reservationCounter = 1;

    public void addReservation(String guestName, String roomType, String roomId) {
        String reservationId = "RES-" + reservationCounter++;
        Reservation reservation = new Reservation(reservationId, guestName, roomType, roomId);
        reservations.add(reservation);
        System.out.println("Added to history: " + reservation.getReservationId() + " for " + guestName);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public int getTotalReservations() {
        return reservations.size();
    }

    public List<Reservation> getReservationsByRoomType(String roomType) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.getRoomType().equals(roomType)) {
                result.add(res);
            }
        }
        return result;
    }

    public List<Reservation> getReservationsByGuest(String guestName) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.getGuestName().equals(guestName)) {
                result.add(res);
            }
        }
        return result;
    }
}

class BookingReportService {
    public void displayBookingHistory(BookingHistory history) {
        System.out.println("\n--- Complete Booking History ---");
        List<Reservation> reservations = history.getAllReservations();
        for (Reservation res : reservations) {
            System.out.println(res.getReservationId() + " | Guest: " + res.getGuestName() +
                    " | Room Type: " + res.getRoomType() + " | Room ID: " + res.getRoomId());
        }
    }

    public void displaySummaryReport(BookingHistory history, AddOnServiceManagerUC8 serviceManager) {
        System.out.println("\n--- Summary Report ---");
        List<Reservation> reservations = history.getAllReservations();
        double totalRevenue = 0;
        int totalBookings = reservations.size();

        for (Reservation res : reservations) {
            double servicesCost = serviceManager.getTotalServiceCost(res.getRoomId());
            totalRevenue += servicesCost;
        }

        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Add-On Revenue: $" + String.format("%.2f", totalRevenue));
        System.out.println("Average Add-On Revenue per Booking: $" + 
            String.format("%.2f", totalBookings > 0 ? totalRevenue / totalBookings : 0));
    }

    public void displayRoomTypeReport(BookingHistory history) {
        System.out.println("\n--- Room Type Report ---");
        Map<String, Integer> roomTypeCounts = new HashMap<>();

        for (Reservation res : history.getAllReservations()) {
            roomTypeCounts.put(res.getRoomType(), 
                roomTypeCounts.getOrDefault(res.getRoomType(), 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : roomTypeCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " bookings");
        }
    }

    public void displayGuestReport(BookingHistory history) {
        System.out.println("\n--- Guest Report ---");
        Map<String, Integer> guestCounts = new HashMap<>();

        for (Reservation res : history.getAllReservations()) {
            guestCounts.put(res.getGuestName(), 
                guestCounts.getOrDefault(res.getGuestName(), 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : guestCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " reservation(s)");
        }
    }
}

class InventoryServiceUC8 {
    private Map<String, Integer> roomInventory = new HashMap<>();
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private Set<String> allAllocatedRooms = new HashSet<>();
    private int roomIdCounter = 1;

    public void addRoomType(String roomType, int count) {
        roomInventory.put(roomType, count);
        allocatedRooms.put(roomType, new HashSet<>());
    }

    public boolean isAvailable(String roomType) {
        return roomInventory.getOrDefault(roomType, 0) > 0;
    }

    public String allocateRoom(String roomType) {
        if (!isAvailable(roomType)) {
            return null;
        }
        String roomId;
        do {
            roomId = roomType + "-" + roomIdCounter++;
        } while (allAllocatedRooms.contains(roomId));

        allAllocatedRooms.add(roomId);
        allocatedRooms.get(roomType).add(roomId);
        roomInventory.put(roomType, roomInventory.get(roomType) - 1);
        return roomId;
    }

    public void displayInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> entry : roomInventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " available");
        }
    }

    public void displayAllocations() {
        System.out.println("Allocated Rooms:");
        for (Map.Entry<String, Set<String>> entry : allocatedRooms.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

class AddOnServiceUC8 {
    String serviceName;
    double cost;

    public AddOnServiceUC8(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " ($" + String.format("%.2f", cost) + ")";
    }
}

class AddOnServiceManagerUC8 {
    private Map<String, List<AddOnServiceUC8>> reservationServices = new HashMap<>();

    public void addServiceToReservation(String reservationId, AddOnServiceUC8 service) {
        reservationServices.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
        System.out.println("Added service: " + service.getServiceName() + " to reservation " + reservationId);
    }

    public List<AddOnServiceUC8> getServicesForReservation(String reservationId) {
        return reservationServices.getOrDefault(reservationId, new ArrayList<>());
    }

    public double getTotalServiceCost(String reservationId) {
        List<AddOnServiceUC8> services = getServicesForReservation(reservationId);
        double totalCost = 0.0;
        for (AddOnServiceUC8 service : services) {
            totalCost += service.getCost();
        }
        return totalCost;
    }

    public void displayReservationServices() {
        for (Map.Entry<String, List<AddOnServiceUC8>> entry : reservationServices.entrySet()) {
            String reservationId = entry.getKey();
            List<AddOnServiceUC8> services = entry.getValue();
            double totalCost = getTotalServiceCost(reservationId);

            System.out.println("\nReservation: " + reservationId);
            System.out.println("  Services:");
            for (AddOnServiceUC8 service : services) {
                System.out.println("    - " + service);
            }
            System.out.println("  Total Additional Cost: $" + String.format("%.2f", totalCost));
        }
    }
}

class BookingServiceUC8 {
    private Queue<BookingRequestUC8> bookingQueue = new LinkedList<>();
    private InventoryServiceUC8 inventory;
    private AddOnServiceManagerUC8 serviceManager;
    private BookingHistory bookingHistory;

    public BookingServiceUC8(InventoryServiceUC8 inventory, AddOnServiceManagerUC8 serviceManager,
                             BookingHistory bookingHistory) {
        this.inventory = inventory;
        this.serviceManager = serviceManager;
        this.bookingHistory = bookingHistory;
    }

    public void addBookingRequest(BookingRequestUC8 request) {
        bookingQueue.add(request);
        System.out.println("Added booking request: " + request);
    }

    public void processBookings() {
        System.out.println("\nProcessing booking requests...");
        while (!bookingQueue.isEmpty()) {
            BookingRequestUC8 request = bookingQueue.poll();
            System.out.println("Processing: " + request);

            if (inventory.isAvailable(request.roomType)) {
                String roomId = inventory.allocateRoom(request.roomType);
                if (roomId != null) {
                    System.out.println("Reservation confirmed for " + request.guestName + " with room ID: " + roomId);
                    // Add to booking history
                    bookingHistory.addReservation(request.guestName, request.roomType, roomId);
                } else {
                    System.out.println("Failed to allocate room for " + request.guestName + " (no available rooms)");
                }
            } else {
                System.out.println("No availability for " + request.roomType + " for " + request.guestName);
            }
        }
    }
}
