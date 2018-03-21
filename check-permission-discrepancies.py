import sys
import json

def get_name(permission_set):
    return permission_set['permissionName']

def get_module_descriptor():
    with open('target/ModuleDescriptor.json') as descriptor_file:
        return json.load(descriptor_file)

descriptor = get_module_descriptor()

permission_sets = descriptor['permissionSets']
permission_set_names = map(get_name, permission_sets)

all_permission_set = filter(lambda s: s['permissionName']=='inventory-storage.all', permission_sets)[0]

sub_permission_names = all_permission_set['subPermissions']

print '\nevery permission defined'
print '-' * 50

for name in permission_set_names:
    print name

print '\npermissions in inventory-storage.all'
print '-' * 50

for name in sub_permission_names:
    print name

with open('every-permission.txt', 'w') as every_permission_file:
    every_permission_file.write('\n'.join(permission_set_names))

with open('sub-permissions.txt', 'w') as every_permission_file:
    every_permission_file.write('\n'.join(sub_permission_names))
