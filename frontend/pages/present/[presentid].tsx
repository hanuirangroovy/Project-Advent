import { useRouter } from "next/router";
import { SetStateAction, useEffect, useState } from "react";
import { Button, Grid, Header, Icon, Input, Popup } from "semantic-ui-react";
import notify from "../../src/component/notify/notify";
import PresentOne from "../../src/component/present/presentone";
import PresentSeven from "../../src/component/present/presentseven";
import PresentThree from "../../src/component/present/presentthree";
import allAxios from "../../src/lib/allAxios";
import styles from "../../styles/present/password.module.css"

export default function Present(){

    const router = useRouter()
    const presentUrl = router.query.presentid

    const [hint, setHint] = useState('')
    const [password, setPassword] = useState('')
    const [openPresent, setOpenPresent] =useState(false)
    const [adventDay, setAdventDay] = useState(0)

    const {Row, Column} = Grid

    const writePassword = (e: { target: { value: SetStateAction<string>; }; }) => {
        setPassword(e.target.value)
    }

    const enterPassword = (e: { code: string; }) => {
        if (e.code === 'Enter') {
            submitPassword()
        }
    }

    const submitPassword = () => {
        checkPassword()
        setPassword('')
        const inputText: any = document.getElementsByName('inputtext')[0]
        inputText['value'] = ''
    }

    const loadIsPassword = async () => {
        await allAxios
            .get(`/advents/${presentUrl}/hints`)
            .then(({ data }) => {
                console.log(data)
                if (data.password === false){
                    setOpenPresent(true)
                    setAdventDay(data.day)
                } else {
                    setHint(data.password_hint)
                }
            })
            .catch((e) => {
                console.log(e)
            })
    }

    const checkPassword = async () => {
        const body = {
            password: password,
            url: presentUrl
        }
        await allAxios
            .post(`/advents/auths`, body)
            .then(({ data }) => {
                setOpenPresent(true)
                setAdventDay(data.day)
            })
            .catch(() => {
                notify('error', `잘못된 비밀번호 입니다.`)
            })
    }

    useEffect(() => {
        if (presentUrl) {
            loadIsPassword()
        }
    }, [presentUrl])

    return(
        <>
            {!openPresent?
            <div className={styles.marginTop} data-aos="zoom-in">
                <Grid centered stackable>
                    <Row>
                        <Column textAlign="center">
                            <Header as="h1" className={ styles.inline }>
                                <span className={ styles.title1 }>선물 비밀번호</span>를 <span className={ styles.title2 }>입력하세요!</span>
                            </Header>&nbsp;
                            <Popup content="비밀번호를 맞춰야 선물을 확인할 수 있습니다." trigger={<Icon name='question circle' color='teal' className={ styles.pointer }/>}/>
                        </Column>
                    </Row>
                    {hint?
                        <>
                            <Row>
                                <Header as="h4" className={ styles.inline }>힌트: { hint }</Header>&nbsp;
                                <Popup content="비밀번호 힌트입니다." trigger={<Icon name='question circle' color='teal' className={ styles.pointer }/>}/>
                            </Row>
                        </>
                    :''}
                    <Row>
                        <Column textAlign="center">
                            <div className={ styles.formField }>
                                <input 
                                    type="password" 
                                    placeholder="비밀번호를 입력해주세요." 
                                    className={password ? styles.formInputIsActive:styles.formInput}
                                    required 
                                    onChange={writePassword} 
                                    onKeyUp={enterPassword}
                                    name="inputtext"
                                />
                            <button className={ styles.btn } onClick={submitPassword}>입력</button>
                            </div>
                        </Column>
                    </Row>
                </Grid>  
            </div>
            :''}
            
            {openPresent?
                adventDay === 1?
                    <PresentOne  data-aos="zoom-in" />
                :adventDay === 3?
                    <PresentThree data-aos="zoom-in" />
                :adventDay === 7?
                    <PresentSeven data-aos="zoom-in" />
                :""
            :''}
        </>
    );
}