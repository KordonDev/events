provider "azurerm" {
  version         = "=2.13.0"
  subscription_id = var.subscription_id
  features {}
}

resource "azurerm_resource_group" "main" {
  name     = "${var.prefix}-resources"
  location = var.location
}