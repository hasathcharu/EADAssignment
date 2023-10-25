using Microsoft.EntityFrameworkCore;
using System.Reflection.Emit;

namespace InventoryManagement.Data
{
    public class DataContext : DbContext
    {
        public DataContext(DbContextOptions<DataContext> options) :base(options)
        {
            
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            base.OnConfiguring(optionsBuilder);
            optionsBuilder.UseSqlServer("Server=localhost;Database=inventoryService;Trusted_Connection=false;TrustServerCertificate=true;User Id=sa;Password=Haritha@123;");    
        }

        public DbSet<InventoryModel> Inventory { get; set; }
    }
}
