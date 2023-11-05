using InventoryManagement.Models;
using InventoryManagement.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using InventoryManagement.DTO;

namespace InventoryManagement.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class InventoryController : ControllerBase
    {
        private readonly IInventoryService _inventoryService;

        public InventoryController(IInventoryService inventoryService)
        {
            _inventoryService = inventoryService;
        }
        [HttpGet]

        public async Task<ActionResult<List<InventoryModel>>> GetAllProducts()
        {
            return await _inventoryService.GetAllProducts();
        }

        [HttpGet ("{PID}")]

        public async Task<ActionResult<InventoryModel>> GetSingleProduct(int PID)
        {
            var result = await _inventoryService.GetSingleProduct(PID);
            if (result is null)
            {
                return NotFound("Product not found");
            }
            return Ok(result);
        }

        [HttpPost]

        public async Task<ActionResult> AddProduct(InventoryModel product)
        {
            var result = await _inventoryService.AddProduct(product);
            if (result == true)
            {
                return Ok("Success");
            }
            return StatusCode(500);
        }

          [HttpPut("{PID}")]

          public async Task<ActionResult> UpdateProduct(long PID, InventoryModel request)
         {
           var result = await _inventoryService.UpdateProduct(PID, request);
           if (result is false)
           {
              return NotFound("Product not found");
           }
          
           return Ok("Success");
         }

        [HttpPut("CancelOrder")]

        public async Task<ActionResult> CancelOrder(List<OrderDTO> products)
        {
            var result = await _inventoryService.CancelOrder(products);
            if (result == false)
            {
                return StatusCode(500);
            }
            return Ok("Success");
        }

        [HttpPut("PlaceOrder")]

        public async Task<ActionResult<List<OrderDTO>>> PlaceOrder(List<OrderDTO> products)
        {
            Console.WriteLine(products);
            System.Diagnostics.Debug.WriteLine("SomeText");
            var result = await _inventoryService.PlaceOrder(products);
            if (result is null)
            {
                return NotFound("Product(s) not in stock");
            }
            return Ok(result);
        }

        [HttpDelete ("{ID}")]

        public async Task<ActionResult> DeleteProduct( long ID)
        {
            var result = await _inventoryService.DeleteProduct(ID);
            if(result == false) 
            {
                return NotFound("Product not found");
            }
            return Ok("Success");
        }
    }
}
