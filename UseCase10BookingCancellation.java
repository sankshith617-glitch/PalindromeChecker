import java.util.*;

public class UseCase10BookingCancellation {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay App ");
        System.out.println(" Version: 1.0.10 (UC10)");
        System.out.println(" Module: Booking Cancellation & Inventory Rollback");
        System.out.println("====================================");

        // Initialize Inventory Service
        InventoryServiceUC10 inventory = new InventoryServiceUC10();
        try {
            inventory.addRoomType("Single", 5);
            inventory.addRoomType("Double", 3);
            inventory.addRoomType("Suite", 2);
        } catch (InvalidRoomTypeException e) {
            System.err.println("Error initializing inventory: " + e.getMessage());
            return;
        }

        // Initialize Booking History
        BookingHistoryUC10 bookingHistory = new BookingHistoryUC10();

        // Initialize Add-On Service Manager
        AddOnServiceManagerUC10 serviceManager = new AddOnServiceManagerUC10();

        // Initialize Booking Service with validator
        BookingValidatorUC10 validator = new BookingValidatorUC10();
        BookingServiceUC10 bookingService = new BookingServiceUC10(inventory, serviceManager, bookingHistory, validator);

        // Initialize Cancellation Service
        CancellationServiceUC10 cancellationService = new CancellationServiceUC10(inventory, bookingHistory);

        // Test Case 1: Create valid bookings
        System.out.println("\n====================================");
        System.out.println(" Test Case 1: Creating Valid Bookings");
        System.out.println("====================================");
        
        try {
            bookingService.addBookingRequest(new BookingRequestUC10("Alice", "Single"));
            bookingService.addBookingRequest(new BookingRequestUC10("Bob", "Double"));
            bookingService.addBookingRequest(new BookingRequestUC10("Charlie", "Single"));
            bookingService.addBookingRequest(new BookingRequestUC10("Diana", "Suite"));
            bookingService.addBookingRequest(new BookingRequestUC10("Eve", "Double"));
        } catch (InvalidBookingException e) {
            System.err.println("Booking error: " + e.getMessage());
        }

        // Process booking requests
        System.out.println("\n====================================");
        System.out.println(" Processing Booking Requests");
        System.out.println("====================================");
        
        bookingService.processBookings();

        // Display initial inventory
        System.out.println();
        inventory.displayInventory();
        inventory.displayAllocations();

        // Add services to some reservations
        System.out.println("\n====================================");
        System.out.println(" Adding Optional Services");
        System.out.println("====================================");
        
        try {
            serviceManager.addServiceToReservation("Single-1", new AddOnServiceUC10("WiFi Premium", 15.0));
            serviceManager.addServiceToReservation("Single-1", new AddOnServiceUC10("Breakfast Buffet", 25.0));
            serviceManager.addServiceToReservation("Double-2", new AddOnServiceUC10("Airport Transfer", 30.0));
        } catch (InvalidServiceException e) {
            System.err.println("Service error: " + e.getMessage());
        }

        serviceManager.displayReservationServices();

        // Display booking history before cancellations
        System.out.println("\n====================================");
        System.out.println(" Booking History Before Cancellations");
        System.out.println("====================================");
        
        BookingReportServiceUC10 reportService = new BookingReportServiceUC10();
        reportService.displayBookingHistory(bookingHistory);

        // Test Case 2: Cancel a valid booking
        System.out.println("\n====================================");
        System.out.println(" Test Case 2: Valid Cancellation");
        System.out.println("====================================");
        
        try {
            cancellationService.cancelReservation("Single-1");
        } catch (CancellationException e) {
            System.err.println("Cancellation error: " + e.getMessage());
        }

        // Display inventory after first cancellation
        System.out.println();
        inventory.displayInventory();
        inventory.displayAllocations();
        cancellationService.displayReleasedRooms();

        // Test Case 3: Try to cancel non-existent booking
        System.out.println("\n====================================");
        System.out.println(" Test Case 3: Cancel Non-Existent Booking");
        System.out.println("====================================");
        
        try {
            cancellationService.cancelReservation("Single-99");
        } catch (CancellationException e) {
            System.err.println("Cancellation rejected: " + e.getMessage());
        }

        // Test Case 4: Try to cancel already cancelled booking
        System.out.println("\n====================================");
        System.out.println(" Test Case 4: Duplicate Cancellation");
        System.out.println("====================================");
        
        try {
            cancellationService.cancelReservation("Single-1");
        } catch (CancellationException e) {
            System.err.println("Cancellation rejected: " + e.getMessage());
        }

        // Test Case 5: Cancel another valid booking
        System.out.println("\n====================================");
        System.out.println(" Test Case 5: Cancel Another Valid Booking");
        System.out.println("====================================");
        
        try {
            cancellationService.cancelReservation("Double-2");
        } catch (CancellationException e) {
            System.err.println("Cancellation error: " + e.getMessage());
        }

        // Display final inventory
        System.out.println();
        inventory.displayInventory();
        inventory.displayAllocations();
        cancellationService.displayReleasedRooms();

        // Display final booking history with cancellations
        System.out.println("\n====================================");
        System.out.println(" Final Booking History");
        System.out.println("====================================");
        
        reportService.displayBookingHistory(bookingHistory);

        // Display operational report
        System.out.println("\n====================================");
        System.out.println(" Operational Report");
        System.out.println("====================================");
        
        reportService.displaySummaryReport(bookingHistory, serviceManager);

        System.out.println("\n====================================");
        System.out.println(" System Status: Stable and Consistent");
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

class CancellationException extends Exception {
    public CancellationException(String message) {
        super(message);
    }
}

class BookingRequestUC10 {
    String guestName;
    String roomType;

    public BookingRequestUC10(String guestName, String roomType) throws InvalidBookingException {
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

class ReservationUC10 {
    String reservationId;
    String guestName;
    String roomType;
    String roomId;
    long confirmationTime;
    boolean isCancelled;

    public ReservationUC10(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.confirmationTime = System.currentTimeMillis();
        this.isCancelled = false;
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

    public boolean isCancelled() {
        return isCancelled;
    }

    public void markCancelled() {
        this.isCancelled = true;
    }

    @Override
    public String toString() {
        String status = isCancelled ? "CANCELLED" : "CONFIRMED";
        return reservationId + " | Guest: " + guestName + " | Room: " + roomId + 
               " | Type: " + roomType + " | Status: " + status;
    }
}

class BookingHistoryUC10 {
    private List<ReservationUC10> reservations = new ArrayList<>();
    private Map<String, ReservationUC10> reservationMap = new HashMap<>();
    private int reservationCounter = 1;

    public void addReservation(String guestName, String roomType, String roomId) {
        String reservationId = "RES-" + reservationCounter++;
        ReservationUC10 reservation = new ReservationUC10(reservationId, guestName, roomType, roomId);
        reservations.add(reservation);
        reservationMap.put(roomId, reservation);
        System.out.println("Added to history: " + reservation.getReservationId() + " for " + guestName);
    }

    public ReservationUC10 getReservationByRoomId(String roomId) {
        return reservationMap.get(roomId);
    }

    public void markReservationCancelled(String roomId) throws CancellationException {
        ReservationUC10 reservation = reservationMap.get(roomId);
        if (reservation == null) {
            throw new CancellationException("Reservation with room ID '" + roomId + "' not found");
        }
        if (reservation.isCancelled()) {
            throw new CancellationException("Reservation " + reservation.getReservationId() + 
                    " is already cancelled");
        }
        reservation.markCancelled();
        System.out.println("Marked reservation " + reservation.getReservationId() + " as cancelled");
    }

    public List<ReservationUC10> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public int getTotalReservations() {
        return reservations.size();
    }

    public int getActiveReservations() {
        int count = 0;
        for (ReservationUC10 res : reservations) {
            if (!res.isCancelled()) {
                count++;
            }
        }
        return count;
    }
}

class BookingValidatorUC10 {
    private Set<String> validRoomTypes = new HashSet<>();

    public void setValidRoomTypes(Set<String> roomTypes) {
        this.validRoomTypes = new HashSet<>(roomTypes);
    }

    public void validateBookingRequest(BookingRequestUC10 request) throws InvalidBookingException {
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

class InventoryServiceUC10 {
    private Map<String, Integer> roomInventory = new HashMap<>();
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private Set<String> allAllocatedRooms = new HashSet<>();
    private int roomIdCounter = 1;
    private BookingValidatorUC10 validator;

    public InventoryServiceUC10() {
        this.validator = new BookingValidatorUC10();
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

    public void releaseRoom(String roomId, String roomType) throws InvalidRoomTypeException {
        if (!allAllocatedRooms.contains(roomId)) {
            throw new InvalidRoomTypeException("Room ID '" + roomId + "' is not allocated");
        }
        
        allAllocatedRooms.remove(roomId);
        allocatedRooms.get(roomType).remove(roomId);
        
        int newCount = roomInventory.get(roomType) + 1;
        roomInventory.put(roomType, newCount);
        System.out.println("Released room " + roomId + " back to inventory");
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

    public BookingValidatorUC10 getValidator() {
        return validator;
    }
}

class AddOnServiceUC10 {
    String serviceName;
    double cost;

    public AddOnServiceUC10(String serviceName, double cost) throws InvalidServiceException {
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

class AddOnServiceManagerUC10 {
    private Map<String, List<AddOnServiceUC10>> reservationServices = new HashMap<>();

    public void addServiceToReservation(String reservationId, AddOnServiceUC10 service) throws InvalidServiceException {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new InvalidServiceException("Reservation ID cannot be null or empty");
        }
        if (service == null) {
            throw new InvalidServiceException("Service cannot be null");
        }
        reservationServices.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
        System.out.println("Added service: " + service.getServiceName() + " to reservation " + reservationId);
    }

    public List<AddOnServiceUC10> getServicesForReservation(String reservationId) {
        return reservationServices.getOrDefault(reservationId, new ArrayList<>());
    }

    public double getTotalServiceCost(String reservationId) {
        List<AddOnServiceUC10> services = getServicesForReservation(reservationId);
        double totalCost = 0.0;
        for (AddOnServiceUC10 service : services) {
            totalCost += service.getCost();
        }
        return totalCost;
    }

    public void displayReservationServices() {
        if (reservationServices.isEmpty()) {
            System.out.println("No services added.");
            return;
        }
        for (Map.Entry<String, List<AddOnServiceUC10>> entry : reservationServices.entrySet()) {
            String reservationId = entry.getKey();
            List<AddOnServiceUC10> services = entry.getValue();
            double totalCost = getTotalServiceCost(reservationId);

            System.out.println("\nReservation: " + reservationId);
            System.out.println("  Services:");
            for (AddOnServiceUC10 service : services) {
                System.out.println("    - " + service);
            }
            System.out.println("  Total Additional Cost: $" + String.format("%.2f", totalCost));
        }
    }
}

class CancellationServiceUC10 {
    private InventoryServiceUC10 inventory;
    private BookingHistoryUC10 bookingHistory;
    private Stack<String> releasedRooms = new Stack<>();

    public CancellationServiceUC10(InventoryServiceUC10 inventory, BookingHistoryUC10 bookingHistory) {
        this.inventory = inventory;
        this.bookingHistory = bookingHistory;
    }

    public void cancelReservation(String roomId) throws CancellationException {
        // Validate reservation exists and is not already cancelled
        ReservationUC10 reservation = bookingHistory.getReservationByRoomId(roomId);
        
        try {
            bookingHistory.markReservationCancelled(roomId);
        } catch (CancellationException e) {
            throw e;
        }

        // Release room back to inventory
        try {
            inventory.releaseRoom(roomId, reservation.getRoomType());
            releasedRooms.push(roomId);
            System.out.println("Cancellation confirmed for " + reservation.getReservationId());
        } catch (InvalidRoomTypeException e) {
            System.err.println("Rollback error: " + e.getMessage());
            throw new CancellationException("Failed to complete cancellation: " + e.getMessage());
        }
    }

    public void displayReleasedRooms() {
        System.out.println("Released Rooms (LIFO Stack):");
        if (releasedRooms.isEmpty()) {
            System.out.println("No released rooms yet.");
            return;
        }
        Stack<String> temp = new Stack<>();
        while (!releasedRooms.isEmpty()) {
            String room = releasedRooms.pop();
            System.out.println("  - " + room);
            temp.push(room);
        }
        while (!temp.isEmpty()) {
            releasedRooms.push(temp.pop());
        }
    }
}

class BookingReportServiceUC10 {
    public void displayBookingHistory(BookingHistoryUC10 history) {
        System.out.println("\n--- Complete Booking History ---");
        List<ReservationUC10> reservations = history.getAllReservations();
        for (ReservationUC10 res : reservations) {
            System.out.println(res);
        }
    }

    public void displaySummaryReport(BookingHistoryUC10 history, AddOnServiceManagerUC10 serviceManager) {
        System.out.println("\n--- Summary Report ---");
        List<ReservationUC10> reservations = history.getAllReservations();
        double totalRevenue = 0;
        int totalBookings = reservations.size();
        int activeBookings = history.getActiveReservations();
        int cancelledBookings = totalBookings - activeBookings;

        for (ReservationUC10 res : reservations) {
            if (!res.isCancelled()) {
                double servicesCost = serviceManager.getTotalServiceCost(res.getRoomId());
                totalRevenue += servicesCost;
            }
        }

        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Active Bookings: " + activeBookings);
        System.out.println("Cancelled Bookings: " + cancelledBookings);
        System.out.println("Total Add-On Revenue: $" + String.format("%.2f", totalRevenue));
        System.out.println("Average Add-On Revenue per Active Booking: $" + 
            String.format("%.2f", activeBookings > 0 ? totalRevenue / activeBookings : 0));
    }
}

class BookingServiceUC10 {
    private Queue<BookingRequestUC10> bookingQueue = new LinkedList<>();
    private InventoryServiceUC10 inventory;
    private AddOnServiceManagerUC10 serviceManager;
    private BookingHistoryUC10 bookingHistory;
    private BookingValidatorUC10 validator;

    public BookingServiceUC10(InventoryServiceUC10 inventory, AddOnServiceManagerUC10 serviceManager,
                             BookingHistoryUC10 bookingHistory, BookingValidatorUC10 validator) {
        this.inventory = inventory;
        this.serviceManager = serviceManager;
        this.bookingHistory = bookingHistory;
        this.validator = inventory.getValidator();
    }

    public void addBookingRequest(BookingRequestUC10 request) throws InvalidBookingException {
        validator.validateBookingRequest(request);
        bookingQueue.add(request);
        System.out.println("Added booking request: " + request);
    }

    public void processBookings() {
        System.out.println("Processing booking requests...");
        while (!bookingQueue.isEmpty()) {
            BookingRequestUC10 request = bookingQueue.poll();
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
