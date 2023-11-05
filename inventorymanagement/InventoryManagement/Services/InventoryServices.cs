using System.Transactions;
using InventoryManagement.DTO;
using Microsoft.EntityFrameworkCore;


namespace InventoryManagement.Services
{
    public class InventoryServices : IInventoryService

    {
       
        private readonly DataContext _context;

        public InventoryServices(DataContext context)
        {
            _context = context;
        }

        public async Task<Boolean> AddProduct(InventoryModel product)
        {
            _context.Inventory.Add(product);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<Boolean> DeleteProduct(long ID)
        {
            var product = await _context.Inventory.FindAsync(ID);
            if (product == null)
            {
                return false;
            }
            _context.Inventory.Remove(product);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<List<InventoryModel>> GetAllProducts()
        {
            var products = await _context.Inventory.ToListAsync();
            return products;
        }

        public async Task<InventoryModel?> GetSingleProduct(long PId)
        {
            var product = await _context.Inventory.FindAsync(PId);

            if (product == null)
            {
                return null;
            }
            return product;
        }

        public async Task<Boolean> UpdateProduct(long PId, InventoryModel request)
        {
            var product = await _context.Inventory.FindAsync(PId);

            if (product == null)
            {
                return false;
            }
            if (request.Product_Name is not null)
            {
                product.Product_Name = request.Product_Name;
            }
            if(request.Product_Brand is not null)
            {
                product.Product_Brand = request.Product_Brand;
            }
            if(request.Price is not null)
            {
                product.Price = request.Price;
            }
            if(request.Available_Quantity is not null)
            {
                product.Available_Quantity = request.Available_Quantity;

            }
            await _context.SaveChangesAsync();

            return true;
        }
        public async Task<Boolean> CancelOrder(List<OrderDTO> products)
        {

            using var scope = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);
            try
            {
                foreach (OrderDTO product in products)
                {
                    var productItem = await _context.Inventory.FindAsync(product.pid);
                    if (productItem is not null)
                    {
                        productItem.Available_Quantity += product.qty;
                    }
                }
                await _context.SaveChangesAsync();
                scope.Complete();
            }
            catch (Exception ex)
            {
                scope.Dispose();
                return false;
            }
            return true;
        }
        public async Task<List<OrderDTO>?> PlaceOrder(List<OrderDTO> products)
        {


            using (var scope = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled))
            {
                try
                {
                    foreach (OrderDTO product in products)
                    {
                        var productItem = await _context.Inventory.FindAsync(product.pid);
                        if (productItem is null)
                        {
                            throw new Exception("Not found");
                        }

                        if (productItem.Available_Quantity >= product.qty)
                        {
                            productItem.Available_Quantity -= product.qty;
                        }
                        else
                        {
                            throw new Exception("Not in stock");
                        }
                        product.price = productItem.Price;
                        product.availableQty = productItem.Available_Quantity;
                    }
                    await _context.SaveChangesAsync();
                    scope.Complete();
                }
                catch (Exception ex)
                {
                    scope.Dispose();
                    return null;
                }
            }
            return products;
        }
    }
}
