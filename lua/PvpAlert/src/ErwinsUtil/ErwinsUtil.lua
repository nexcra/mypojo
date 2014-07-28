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
    print('size 0')
    return
  end
  for key in pairs(table) do
    print(key..' = '..t2[key])
  end
end