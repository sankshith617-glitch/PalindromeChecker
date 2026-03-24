import java.util.*;

public class UseCase7AddOnServiceSelection {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay App ");
        System.out.println(" Version: 1.0.7 (UC7)");
        System.out.println(" Module: Add-On Service Selection");
        System.out.println("====================================");

        // Initialize Inventory Service
        InventoryServiceUC7 inventory = new InventoryServiceUC7();
        inventory.addRoomType("Single", 5);
        inventory.addRoomType("Double", 3);
        inventory.addRoomType("Suite", 2);

        // Initialize Add-On Service Manager
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Initialize Booking Service
        BookingServiceUC7 bookingService = new BookingServiceUC7(inventory, serviceManager);

        // Add some booking requests
        bookingService.addBookingRequest(new BookingRequestUC7("Alice", "Single"));
        bookingService.addBookingRequest(new BookingRequestUC7("Bob", "Double"));
        bookingService.addBookingRequest(new BookingRequestUC7("Charlie", "Single"));
        bookingService.addBookingRequest(new BookingRequestUC7("Diana", "Suite"));

        // Process booking requests
        bookingService.processBookings();

        // Display final inventory and allocations
        inventory.displayInventory();
        inventory.displayAllocations();

        // Add services to reservations
        System.out.println("\n====================================");
        System.out.println(" Adding Optional Services");
        System.out.println("====================================");

        serviceManager.addServiceToReservation("Single-1", new AddOnService("WiFi Premium", 15.0));
        serviceManager.addServiceToReservation("Single-1", new AddOnService("Breakfast Buffet", 25.0));
        serviceManager.addServiceToReservation("Double-2", new AddOnService("Airport Transfer", 30.0));
        serviceManager.addServiceToReservation("Suite-3", new AddOnService("WiFi Premium", 15.0));
        serviceManager.addServiceToReservation("Suite-3", new AddOnService("Spa Package", 100.0));
        serviceManager.addServiceToReservation("Suite-3", new AddOnService("Late Checkout", 20.0));

        // Display services and costs for each reservation
        System.out.println("\n====================================");
        System.out.println(" Reservation Services & Additional Costs");
        System.out.println("====================================");
        serviceManager.displayReservationServices();
    }
}

class BookingRequestUC7 {
    String guestName;
    String roomType;

    public BookingRequestUC7(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "BookingRequest{guestName='" + guestName + "', roomType='" + roomType + "'}";
    }
}

class InventoryServiceUC7 {
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

class AddOnService {
    String serviceName;
    double cost;

    public AddOnService(String serviceName, double cost) {
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

class AddOnServiceManager {
    private Map<String, List<AddOnService>> reservationServices = new HashMap<>();

    public void addServiceToReservation(String reservationId, AddOnService service) {
        reservationServices.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
        System.out.println("Added service: " + service.getServiceName() + " to reservation " + reservationId);
    }

    public List<AddOnService> getServicesForReservation(String reservationId) {
        return reservationServices.getOrDefault(reservationId, new ArrayList<>());
    }

    public double getTotalServiceCost(String reservationId) {
        List<AddOnService> services = getServicesForReservation(reservationId);
        double totalCost = 0.0;
        for (AddOnService service : services) {
            totalCost += service.getCost();
        }
        return totalCost;
    }

    public void displayReservationServices() {
        for (Map.Entry<String, List<AddOnService>> entry : reservationServices.entrySet()) {
            String reservationId = entry.getKey();
            List<AddOnService> services = entry.getValue();
            double totalCost = getTotalServiceCost(reservationId);

            System.out.println("\nReservation: " + reservationId);
            System.out.println("  Services:");
            for (AddOnService service : services) {
                System.out.println("    - " + service);
            }
            System.out.println("  Total Additional Cost: $" + String.format("%.2f", totalCost));
        }
    }
}

class BookingServiceUC7 {
    private Queue<BookingRequestUC7> bookingQueue = new LinkedList<>();
    private InventoryServiceUC7 inventory;
    private AddOnServiceManager serviceManager;

    public BookingServiceUC7(InventoryServiceUC7 inventory, AddOnServiceManager serviceManager) {
        this.inventory = inventory;
        this.serviceManager = serviceManager;
    }

    public void addBookingRequest(BookingRequestUC7 request) {
        bookingQueue.add(request);
        System.out.println("Added booking request: " + request);
    }

    public void processBookings() {
        System.out.println("\nProcessing booking requests...");
        while (!bookingQueue.isEmpty()) {
            BookingRequestUC7 request = bookingQueue.poll();
            System.out.println("Processing: " + request);

            if (inventory.isAvailable(request.roomType)) {
                String roomId = inventory.allocateRoom(request.roomType);
                if (roomId != null) {
                    System.out.println("Reservation confirmed for " + request.guestName + " with room ID: " + roomId);
                } else {
                    System.out.println("Failed to allocate room for " + request.guestName + " (no available rooms)");
                }
            } else {
                System.out.println("No availability for " + request.roomType + " for " + request.guestName);
            }
        }
    }
}
