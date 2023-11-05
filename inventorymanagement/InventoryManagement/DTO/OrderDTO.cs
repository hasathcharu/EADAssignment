namespace InventoryManagement.DTO
{
    public class OrderDTO
    {
        public long pid { get; set; }
        public double? price { get; set; }
        public double? qty { get; set; }
        public double? availableQty { get; set; }
    }
}
