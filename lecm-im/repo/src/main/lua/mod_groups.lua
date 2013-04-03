-- Prosody IM
-- Copyright (C) 2008-2010 Matthew Wild
-- Copyright (C) 2008-2010 Waqas Hussain
-- 
-- This project is MIT/X11 licensed. Please see the
-- COPYING file in the source package for more information.
--


local groups;
local members;

local groups_file;

local jid, datamanager = require "util.jid", require "util.datamanager";
local dm_load = require "util.datamanager".load;
local jid_bare, jid_prep = jid.bare, jid.prep;

local module_host = module:get_host();

function inject_roster_contacts(username, host, roster)
	--module:log("debug", "Injecting group members to roster");
	local bare_jid = username.."@"..host;

	local data_path = CFG_DATADIR or "data";

	if not pcall(require, "luarocks.loader") then
		pcall(require, "luarocks.require");
	end
			
	local lfs = require "lfs";

	local jid;
	
	function decode(s)
		return s:gsub("%%([a-fA-F0-9][a-fA-F0-9])", function (c)
			    return string.char(tonumber("0x"..c));
	        end);
	end
	

	local accounts = data_path.."/".."localhost".."/accounts";
	if lfs.attributes(accounts, "mode") == "directory" then
	    for user in lfs.dir(accounts) do
		    if user:sub(1,1) ~= "." then
			jid = jid_prep(decode(user:gsub("%.dat$", "")).."@"..decode(host));
			if jid then
			    local user_data = dm_load(decode(user:gsub("%.dat$", "")), "localhost", "accounts");
			    module:log("debug", "New member of %s: %s", tostring(curr_group), tostring(jid));
			    groups["default"][jid] = user_data.FN or false; --name or false;
			    members[jid] = members[jid] or {};
			    members[jid][#members[jid]+1] = "default";
			end
			
			
		    end
	    end
        end


	local function import_jids_to_roster(group_name)
		for jid in pairs(groups[group_name]) do
			-- Add them to roster
			--module:log("debug", "processing jid %s in group %s", tostring(jid), tostring(group_name));
			if jid ~= bare_jid then
				if not roster[jid] then roster[jid] = {}; end
				roster[jid].subscription = "both";
				if groups[group_name][jid] then
					roster[jid].name = groups[group_name][jid];
				end
				if not roster[jid].groups then
					roster[jid].groups = { [group_name] = true };
				end
				roster[jid].groups[group_name] = true;
				roster[jid].persist = false;
			end
		end
	end

	-- Find groups this JID is a member of
--	if members[bare_jid] then
--		for _, group_name in ipairs(members[bare_jid]) do
--			--module:log("debug", "Importing group %s", group_name);
--			import_jids_to_roster(group_name);
--		end
--	end
	
	-- Import public groups
--	if members[false] then
--		for _, group_name in ipairs(members[false]) do
--			--module:log("debug", "Importing group %s", group_name);
--			import_jids_to_roster(group_name);
--		end
--	end

	import_jids_to_roster("default");
	
	if roster[false] then
		roster[false].version = true;
	end
end

function remove_virtual_contacts(username, host, datastore, data)
	if host == module_host and datastore == "roster" then
		local new_roster = {};
		for jid, contact in pairs(data) do
			if contact.persist ~= false then
				new_roster[jid] = contact;
			end
		end
		if new_roster[false] then
			new_roster[false].version = nil; -- Version is void
		end
		return username, host, datastore, new_roster;
	end

	return username, host, datastore, data;
end

function module.load()
	groups_file = config.get(module:get_host(), "core", "groups_file");
	if not groups_file then return; end
	
	module:hook("roster-load", inject_roster_contacts);
	datamanager.add_callback(remove_virtual_contacts);
	
	groups = { default = {} };
	members = { };
	local curr_group = "default";
	for line in io.lines(groups_file) do
		if line:match("^%s*%[.-%]%s*$") then
			curr_group = line:match("^%s*%[(.-)%]%s*$");
			if curr_group:match("^%+") then
				curr_group = curr_group:gsub("^%+", "");
				if not members[false] then
					members[false] = {};
				end
				members[false][#members[false]+1] = curr_group; -- Is a public group
			end
			module:log("debug", "New group: %s", tostring(curr_group));
			groups[curr_group] = groups[curr_group] or {};
		else
			-- Add JID
			local entryjid, name = line:match("([^=]*)=?(.*)");
			module:log("debug", "entryjid = '%s', name = '%s'", entryjid, name);
			local jid;
			jid = jid_prep(entryjid:match("%S+"));
			if jid then
				module:log("debug", "New member of %s: %s", tostring(curr_group), tostring(jid));
				groups[curr_group][jid] = name or false;
				members[jid] = members[jid] or {};
				members[jid][#members[jid]+1] = curr_group;
			end
		end
	end
	module:log("info", "Groups loaded successfully");
end

function module.unload()
	datamanager.remove_callback(remove_virtual_contacts);
end
