import java.util.*;

public class UseCase9ErrorHandlingValidation {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay App ");
        System.out.println(" Version: 1.0.9 (UC9)");
        System.out.println(" Module: Error Handling & Validation");
        System.out.println("====================================");

        // Initialize Inventory Service
        InventoryServiceUC9 inventory = new InventoryServiceUC9();
        try {
            inventory.addRoomType("Single", 5);
            inventory.addRoomType("Double", 3);
            inventory.addRoomType("Suite", 2);
        } catch (InvalidRoomTypeException e) {
            System.err.println("Error initializing inventory: " + e.getMessage());
            return;
        }

        // Initialize Booking History
        BookingHistoryUC9 bookingHistory = new BookingHistoryUC9();

        // Initialize Add-On Service Manager
        AddOnServiceManagerUC9 serviceManager = new AddOnServiceManagerUC9();

        // Initialize Booking Service with validator from inventory service
        BookingValidatorUC9 validator = inventory.getValidator();
        BookingServiceUC9 bookingService = new BookingServiceUC9(inventory, serviceManager, bookingHistory, validator);

        // Test Case 1: Valid bookings
        System.out.println("\n====================================");
        System.out.println(" Test Case 1: Valid Bookings");
        System.out.println("====================================");
        
        try {
            bookingService.addBookingRequest(new BookingRequestUC9("Alice", "Single"));
            bookingService.addBookingRequest(new BookingRequestUC9("Bob", "Double"));
            bookingService.addBookingRequest(new BookingRequestUC9("Charlie", "Single"));
            bookingService.addBookingRequest(new BookingRequestUC9("Diana", "Suite"));
        } catch (InvalidBookingException e) {
            System.err.println("Booking error: " + e.getMessage());
        }

        // Test Case 2: Invalid room type
        System.out.println("\n====================================");
        System.out.println(" Test Case 2: Invalid Room Type");
        System.out.println("====================================");
        
        try {
            bookingService.addBookingRequest(new BookingRequestUC9("Eve", "Penthouse"));
        } catch (InvalidBookingException e) {
            System.err.println("Booking rejected: " + e.getMessage());
        }

        // Test Case 3: Empty guest name
        System.out.println("\n====================================");
        System.out.println(" Test Case 3: Empty Guest Name");
        System.out.println("====================================");
        
        try {
            bookingService.addBookingRequest(new BookingRequestUC9("", "Double"));
        } catch (InvalidBookingException e) {
            System.err.println("Booking rejected: " + e.getMessage());
        }

        // Test Case 4: Null values
        System.out.println("\n====================================");
        System.out.println(" Test Case 4: Null Values");
        System.out.println("====================================");
        
        try {
            bookingService.addBookingRequest(new BookingRequestUC9(null, "Single"));
        } catch (InvalidBookingException e) {
            System.err.println("Booking rejected: " + e.getMessage());
        }

        // Process booking requests
        System.out.println("\n====================================");
        System.out.println(" Processing Valid Booking Requests");
        System.out.println("====================================");
        
        bookingService.processBookings();

        // Display final inventory and allocations
        inventory.displayInventory();
        inventory.displayAllocations();

        // Add services to reservations
        System.out.println("\n====================================");
        System.out.println(" Adding Optional Services");
        System.out.println("====================================");
        
        try {
            serviceManager.addServiceToReservation("Single-1", new AddOnServiceUC9("WiFi Premium", 15.0));
            serviceManager.addServiceToReservation("Single-1", new AddOnServiceUC9("Breakfast Buffet", 25.0));
            serviceManager.addServiceToReservation("Double-2", new AddOnServiceUC9("Airport Transfer", 30.0));
        } catch (InvalidServiceException e) {
            System.err.println("Service error: " + e.getMessage());
        }

        // Display services and costs
        System.out.println("\n====================================");
        System.out.println(" Reservation Services & Additional Costs");
        System.out.println("====================================");
        
        serviceManager.displayReservationServices();

        // Display booking history and reports
        System.out.println("\n====================================");
        System.out.println(" Booking History & Operational Reports");
        System.out.println("====================================");
        
        BookingReportServiceUC9 reportService = new BookingReportServiceUC9();
        reportService.displayBookingHistory(bookingHistory);
        reportService.displaySummaryReport(bookingHistory, serviceManager);
        reportService.displayRoomTypeReport(bookingHistory);

        // Test Case 5: Invalid operation - attempt to add service with negative cost
        System.out.println("\n====================================");
        System.out.println(" Test Case 5: Invalid Service Cost");
        System.out.println("====================================");
        
        try {
            serviceManager.addServiceToReservation("Single-1", new AddOnServiceUC9("Invalid Service", -50.0));
        } catch (InvalidServiceException e) {
            System.err.println("Service rejected: " + e.getMessage());
        }

        System.out.println("\n====================================");
        System.out.println(" System Status: Stable and Ready");
        System.out.println("====================================");
    }
}

// Custom Exceptions
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class InvalidServiceException extends Exception {
    public InvalidServiceException(String message) {
        super(message);
    }
}

class BookingRequestUC9 {
    String guestName;
    String roomType;

    public BookingRequestUC9(String guestName, String roomType) throws InvalidBookingException {
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be null or empty");
        }
        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be null or empty");
        }
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "BookingRequest{guestName='" + guestName + "', roomType='" + roomType + "'}";
    }
}

class ReservationUC9 {
    String reservationId;
    String guestName;
    String roomType;
    String roomId;
    long confirmationTime;

    public ReservationUC9(String reservationId, String guestName, String roomType, String roomId) {
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
}

class BookingHistoryUC9 {
    private List<ReservationUC9> reservations = new ArrayList<>();
    private int reservationCounter = 1;

    public void addReservation(String guestName, String roomType, String roomId) {
        String reservationId = "RES-" + reservationCounter++;
        ReservationUC9 reservation = new ReservationUC9(reservationId, guestName, roomType, roomId);
        reservations.add(reservation);
        System.out.println("Added to history: " + reservation.getReservationId() + " for " + guestName);
    }

    public List<ReservationUC9> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public int getTotalReservations() {
        return reservations.size();
    }

    public List<ReservationUC9> getReservationsByRoomType(String roomType) {
        List<ReservationUC9> result = new ArrayList<>();
        for (ReservationUC9 res : reservations) {
            if (res.getRoomType().equals(roomType)) {
                result.add(res);
            }
        }
        return result;
    }
}

class BookingValidatorUC9 {
    private Set<String> validRoomTypes = new HashSet<>();

    public void setValidRoomTypes(Set<String> roomTypes) {
        this.validRoomTypes = new HashSet<>(roomTypes);
    }

    public void validateBookingRequest(BookingRequestUC9 request) throws InvalidBookingException {
        if (request.guestName == null || request.guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be null or empty");
        }
        if (request.roomType == null || request.roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be null or empty");
        }
        if (!validRoomTypes.contains(request.roomType)) {
            throw new InvalidBookingException("Invalid room type: '" + request.roomType + 
                    "'. Valid types are: " + validRoomTypes);
        }
    }

    public void validateInventoryState(int availableCount) throws InvalidRoomTypeException {
        if (availableCount < 0) {
            throw new InvalidRoomTypeException("Inventory count cannot be negative");
        }
    }
}

class InventoryServiceUC9 {
    private Map<String, Integer> roomInventory = new HashMap<>();
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private Set<String> allAllocatedRooms = new HashSet<>();
    private int roomIdCounter = 1;
    private BookingValidatorUC9 validator;

    public InventoryServiceUC9() {
        this.validator = new BookingValidatorUC9();
    }

    public void addRoomType(String roomType, int count) throws InvalidRoomTypeException {
        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidRoomTypeException("Room type cannot be null or empty");
        }
        if (count < 0) {
            throw new InvalidRoomTypeException("Room count cannot be negative");
        }
        roomInventory.put(roomType, count);
        allocatedRooms.put(roomType, new HashSet<>());
        validator.setValidRoomTypes(roomInventory.keySet());
    }

    public boolean isAvailable(String roomType) throws InvalidRoomTypeException {
        if (!roomInventory.containsKey(roomType)) {
            throw new InvalidRoomTypeException("Room type does not exist: " + roomType);
        }
        return roomInventory.getOrDefault(roomType, 0) > 0;
    }

    public String allocateRoom(String roomType) throws InvalidRoomTypeException {
        if (!isAvailable(roomType)) {
            return null;
        }
        
        String roomId;
        do {
            roomId = roomType + "-" + roomIdCounter++;
        } while (allAllocatedRooms.contains(roomId));

        allAllocatedRooms.add(roomId);
        allocatedRooms.get(roomType).add(roomId);
        
        int newCount = roomInventory.get(roomType) - 1;
        validator.validateInventoryState(newCount);
        
        roomInventory.put(roomType, newCount);
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

    public BookingValidatorUC9 getValidator() {
        return validator;
    }
}

class AddOnServiceUC9 {
    String serviceName;
    double cost;

    public AddOnServiceUC9(String serviceName, double cost) throws InvalidServiceException {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new InvalidServiceException("Service name cannot be null or empty");
        }
        if (cost < 0) {
            throw new InvalidServiceException("Service cost cannot be negative");
        }
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

class AddOnServiceManagerUC9 {
    private Map<String, List<AddOnServiceUC9>> reservationServices = new HashMap<>();

    public void addServiceToReservation(String reservationId, AddOnServiceUC9 service) throws InvalidServiceException {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new InvalidServiceException("Reservation ID cannot be null or empty");
        }
        if (service == null) {
            throw new InvalidServiceException("Service cannot be null");
        }
        reservationServices.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
        System.out.println("Added service: " + service.getServiceName() + " to reservation " + reservationId);
    }

    public List<AddOnServiceUC9> getServicesForReservation(String reservationId) {
        return reservationServices.getOrDefault(reservationId, new ArrayList<>());
    }

    public double getTotalServiceCost(String reservationId) {
        List<AddOnServiceUC9> services = getServicesForReservation(reservationId);
        double totalCost = 0.0;
        for (AddOnServiceUC9 service : services) {
            totalCost += service.getCost();
        }
        return totalCost;
    }

    public void displayReservationServices() {
        for (Map.Entry<String, List<AddOnServiceUC9>> entry : reservationServices.entrySet()) {
            String reservationId = entry.getKey();
            List<AddOnServiceUC9> services = entry.getValue();
            double totalCost = getTotalServiceCost(reservationId);

            System.out.println("\nReservation: " + reservationId);
            System.out.println("  Services:");
            for (AddOnServiceUC9 service : services) {
                System.out.println("    - " + service);
            }
            System.out.println("  Total Additional Cost: $" + String.format("%.2f", totalCost));
        }
    }
}

class BookingReportServiceUC9 {
    public void displayBookingHistory(BookingHistoryUC9 history) {
        System.out.println("\n--- Complete Booking History ---");
        List<ReservationUC9> reservations = history.getAllReservations();
        for (ReservationUC9 res : reservations) {
            System.out.println(res.getReservationId() + " | Guest: " + res.getGuestName() +
                    " | Room Type: " + res.getRoomType() + " | Room ID: " + res.getRoomId());
        }
    }

    public void displaySummaryReport(BookingHistoryUC9 history, AddOnServiceManagerUC9 serviceManager) {
        System.out.println("\n--- Summary Report ---");
        List<ReservationUC9> reservations = history.getAllReservations();
        double totalRevenue = 0;
        int totalBookings = reservations.size();

        for (ReservationUC9 res : reservations) {
            double servicesCost = serviceManager.getTotalServiceCost(res.getRoomId());
            totalRevenue += servicesCost;
        }

        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Add-On Revenue: $" + String.format("%.2f", totalRevenue));
        System.out.println("Average Add-On Revenue per Booking: $" +
            String.format("%.2f", totalBookings > 0 ? totalRevenue / totalBookings : 0));
    }

    public void displayRoomTypeReport(BookingHistoryUC9 history) {
        System.out.println("\n--- Room Type Report ---");
        Map<String, Integer> roomTypeCounts = new HashMap<>();

        for (ReservationUC9 res : history.getAllReservations()) {
            roomTypeCounts.put(res.getRoomType(),
                roomTypeCounts.getOrDefault(res.getRoomType(), 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : roomTypeCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " bookings");
        }
    }
}

class BookingServiceUC9 {
    private Queue<BookingRequestUC9> bookingQueue = new LinkedList<>();
    private InventoryServiceUC9 inventory;
    private AddOnServiceManagerUC9 serviceManager;
    private BookingHistoryUC9 bookingHistory;
    private BookingValidatorUC9 validator;

    public BookingServiceUC9(InventoryServiceUC9 inventory, AddOnServiceManagerUC9 serviceManager,
                             BookingHistoryUC9 bookingHistory, BookingValidatorUC9 validator) {
        this.inventory = inventory;
        this.serviceManager = serviceManager;
        this.bookingHistory = bookingHistory;
        this.validator = validator;
    }

    public void addBookingRequest(BookingRequestUC9 request) throws InvalidBookingException {
        validator.validateBookingRequest(request);
        bookingQueue.add(request);
        System.out.println("Added booking request: " + request);
    }

    public void processBookings() {
        System.out.println("Processing booking requests...");
        while (!bookingQueue.isEmpty()) {
            BookingRequestUC9 request = bookingQueue.poll();
            System.out.println("Processing: " + request);

            try {
                if (inventory.isAvailable(request.roomType)) {
                    String roomId = inventory.allocateRoom(request.roomType);
                    if (roomId != null) {
                        System.out.println("Reservation confirmed for " + request.guestName + " with room ID: " + roomId);
                        bookingHistory.addReservation(request.guestName, request.roomType, roomId);
                    } else {
                        System.out.println("Failed to allocate room for " + request.guestName + " (no available rooms)");
                    }
                } else {
                    System.out.println("No availability for " + request.roomType + " for " + request.guestName);
                }
            } catch (InvalidRoomTypeException e) {
                System.err.println("Error processing booking for " + request.guestName + ": " + e.getMessage());
            }
        }
    }
}
