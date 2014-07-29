print ('=== ErwinsUtil loaded ===')

-- =========== static 상수 ================
Color = {sky='cff00cccc',pink='cffff66cc',purple='cff00cccc'}

u = {}



--칼라를 입혀준다. 패턴 다시보기
--
u.color = function(color,text)
  return "|".. color ..text.."|r"
end

u.info = function(table)
  local size = #table
  if(size==0) then
    print('- key-value size =  0')
  end
  for key in pairs(table) do
    local value = table[key]
    local type = type(value)
    if(type=='function') then  value = 'function' end 
    print('- ' .. key .. ' = ' .. value)
  end
end