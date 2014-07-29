print ('=== WowUtil loaded ===')

local TextColorFactory = function(colorCode)  
  -- append는 .. 로 이으기 싫어서 썼다
  return function(text,append)
    if(append == nil) then append = '' end 
    return "|".. colorCode ..text.."|r"..append
  end
end

-- WOW에사용되는 컬러타입으로 변경해준다. <br> 다이렉트로 설정해야지 IDE가 인식해준다. ㅠㅠ
-- ex) print(Color.sky('영감님'))
TextColor = {sky=TextColorFactory('cff00cccc'),pink=TextColorFactory('cffff66cc'),purple=TextColorFactory('cff00cccc')}