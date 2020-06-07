resource "azurerm_mysql_server" "main" {
  name                = "${var.prefix}-mysql-server"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name

  sku_name                   = "B_Gen5_1"
  storage_mb = 5120
  backup_retention_days      = 7
  geo_redundant_backup_enabled      = false


  administrator_login          = "mysqladminun"
  administrator_login_password = var.my_sql_master_password
  version                      = "5.7"
  ssl_enforcement_enabled              = true
}

# This is the database that our application will use
resource "azurerm_mysql_database" "main" {
  name                = "${var.prefix}-mysql-db"
  resource_group_name = azurerm_resource_group.main.name
  server_name         = azurerm_mysql_server.main.name
  charset             = "utf8"
  collation           = "utf8_unicode_ci"
}

# This rule is to enable the 'Allow access to Azure services' checkbox
resource "azurerm_mysql_firewall_rule" "main" {
  name                = "${var.prefix}-mysql-firewall"
  resource_group_name = azurerm_resource_group.main.name
  server_name         = azurerm_mysql_server.main.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}