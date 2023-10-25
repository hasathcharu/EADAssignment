using InventoryManagement.DTO;

namespace InventoryManagement.Services
{
    public interface IInventoryService
    {
        Task<List<InventoryModel>> GetAllProducts();

        Task<InventoryModel?> GetSingleProduct(long PID);

       Task<Boolean> AddProduct(InventoryModel product);

        Task<Boolean> UpdateProduct(long PID, InventoryModel request);

        Task<Boolean> DeleteProduct(long ID);

        Task<List<OrderDTO>?> PlaceOrder(List<OrderDTO> products);
        
        Task<Boolean> CancelOrder(List<OrderDTO> products);
        
    }
}
