import java.util.*;

public class UseCase6RoomAllocationService {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay App ");
        System.out.println(" Version: 1.0.6 (UC6)");
        System.out.println(" Module: Reservation Confirmation & Room Allocation");
        System.out.println("====================================");

        // Initialize Inventory Service
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Single", 5);
        inventory.addRoomType("Double", 3);
        inventory.addRoomType("Suite", 2);

        // Initialize Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Add some booking requests
        bookingService.addBookingRequest(new BookingRequest("Alice", "Single"));
        bookingService.addBookingRequest(new BookingRequest("Bob", "Double"));
        bookingService.addBookingRequest(new BookingRequest("Charlie", "Single"));
        bookingService.addBookingRequest(new BookingRequest("Diana", "Suite"));
        bookingService.addBookingRequest(new BookingRequest("Eve", "Single"));
        bookingService.addBookingRequest(new BookingRequest("Frank", "Double"));
        bookingService.addBookingRequest(new BookingRequest("Grace", "Suite"));
        bookingService.addBookingRequest(new BookingRequest("Heidi", "Single")); // This should fail due to inventory

        // Process booking requests
        bookingService.processBookings();

        // Display final inventory and allocations
        inventory.displayInventory();
        inventory.displayAllocations();
    }
}

class BookingRequest {
    String guestName;
    String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "BookingRequest{guestName='" + guestName + "', roomType='" + roomType + "'}";
    }
}

class InventoryService {
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

class BookingService {
    private Queue<BookingRequest> bookingQueue = new LinkedList<>();
    private InventoryService inventory;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void addBookingRequest(BookingRequest request) {
        bookingQueue.add(request);
        System.out.println("Added booking request: " + request);
    }

    public void processBookings() {
        System.out.println("Processing booking requests...");
        while (!bookingQueue.isEmpty()) {
            BookingRequest request = bookingQueue.poll();
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