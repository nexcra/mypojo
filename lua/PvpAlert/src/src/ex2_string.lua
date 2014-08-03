
local Util = require('ErwinsUtil.ErwinsUtil')

i,j = string.find('abcdefg12345','fg12')
print(i)
print(string.sub('abcdefg12345',string.find('abcdefg12345','fg12'))) --굿?

print( string.match("우리동네 300원",'%d+원'))

local replaced = string.gsub("우리동네 300원 / 200원 50원 ",'%d+원','@',2)
print( replaced )