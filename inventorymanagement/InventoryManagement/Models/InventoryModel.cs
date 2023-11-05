using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;

namespace InventoryManagement.Models
{
    public class InventoryModel
    {
        [Key]
        public long PId { get; set; }
        public string? Product_Name { get; set; }
        public string? Product_Brand { get; set; }
        public double? Price { get; set; }
        public double? Available_Quantity { get; set; }

       
    }
}
