<@markup id="DOJO_MODULE_MAPPING_PATCHES" target="setDojoConfig" action="after" scope="global" >

   <script type="text/javascript">
      dojoConfig.map = {
         "*": {
			"alfresco/menus/_AlfMenuItemMixin": "logic_ecm/patches/alfresco/menus/_AlfMenuItemMixin",
            "dojo/touch": "logic_ecm/patches/dojo/touch"
         }
      };
   </script>
</@>